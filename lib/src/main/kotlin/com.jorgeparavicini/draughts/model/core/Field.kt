package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.model.enums.FieldSize
import com.jorgeparavicini.draughts.model.enums.MoveType
import com.jorgeparavicini.draughts.model.enums.Player
import com.jorgeparavicini.draughts.model.exceptions.IllegalMoveException
import mu.KotlinLogging

public typealias GameOverHandler = (Player) -> Unit
public typealias GameResetHandler = () -> Unit

private val logger = KotlinLogging.logger { }

public class Field(private val size: FieldSize = FieldSize.SMALL) {
    internal var winner: Player? = null
        private set

    internal val isGameOver: Boolean
        get() = winner != null

    public val fieldSize: Int
        get() = size.size

    private val numberOfPieces: Int = fieldSize * (fieldSize - 2) / 2

    private val pieces: List<Piece> = List(numberOfPieces) { i ->
        val x = (if (i * 2 / fieldSize % 2 == 0) 1 else 0) + i * 2 % fieldSize
        var y = i * 2 / fieldSize
        val isInBotHalf = i * 2 / fieldSize >= fieldSize / 2 - 1
        if (isInBotHalf) y += 2
        val player = if (isInBotHalf) Player.BLACK else Player.WHITE
        Piece(player, Vector2(x, y))
    }

    private var onGameOver: GameOverHandler? = null

    private var onGameReset: GameResetHandler? = null

    init {
        reset()
    }

    public fun setOnGameOverHandler(handler: GameOverHandler) {
        onGameOver = handler
    }

    public fun setOnGameResetHandler(handler: GameResetHandler) {
        onGameReset = handler
    }

    internal fun reset() {
        resetPieces()
        onGameReset?.invoke()
        logger.trace("Field reset")
    }

    private fun resetPieces() {
        pieces.forEach { it.reset() }
    }

    internal fun executeMove(move: Move, player: Player): Boolean {
        if (move.piece.player != player) throw IllegalMoveException("Can not move enemy piece")
        if (move !in getPossibleMoves(move.piece.player)) throw IllegalMoveException("Move is not allowed")
        val pointsBetween = (move.piece.position..move.destination).drop(1).dropLast(1)
        val eaten = pointsBetween.any {
            val piece = getPiece(it)
            if (piece != null && piece.player != move.piece.player) {
                piece.eaten = true
                return@any true
            }
            false
        }

        val oldPos = move.piece.position
        move.piece.position = move.destination

        logger.trace("Moved ${move.piece} from $oldPos to ${move.destination}")
        updateDraught(move.piece)
        updateGameOver()
        return eaten
    }

    private fun updateDraught(piece: Piece) {
        if (piece.isDraught) return

        if (piece.player == Player.BLACK && piece.position.y == 0 ||
            piece.player == Player.WHITE && piece.position.y == fieldSize - 1
        ) {
            piece.isDraught = true
            logger.trace("Piece $piece promoted")
        }
    }

    private fun updateGameOver() {
        if (getPieces(Player.BLACK).count() == 0) {
            winner = Player.WHITE
            onGameOver?.invoke(winner!!)
            logger.trace("White won")
        } else if (getPieces(Player.WHITE).count() == 0) {
            winner = Player.BLACK
            onGameOver?.invoke(winner!!)
            logger.trace("BLACK won")
        }
    }

    public fun getPossibleMoves(player: Player): List<Move> {
        val pieces = getPieces(player)
        var moves = pieces.map { getPossibleMoves(it) }.flatten()

        if (moves.any { move -> move.second == MoveType.VALID_EAT }) {
            moves = moves.filter { move -> move.second == MoveType.VALID_EAT }
        }
        return moves.map { move -> move.first }
    }

    private fun getPossibleMoves(piece: Piece): List<Pair<Move, MoveType>> {
        if (isGameOver) throw IllegalStateException("Can not get moves when game is already over.")
        val diagonalPositions = piece.position.getDiagonalPositions(fieldSize)

        return diagonalPositions.map { pos ->
            val move = Move(piece, pos)
            return@map Pair(move, getMoveType(move))
        }.filter { it.second != MoveType.INVALID }
    }

    private fun getMoveType(move: Move): MoveType {
        val displacement = move.destination - move.piece.position
        if (!displacement.isDiagonal) return MoveType.INVALID
        if (move.destination == move.piece.position) return MoveType.INVALID

        val magnitude = displacement.diagonalMagnitude
        val isDraught = move.piece.isDraught
        val pointsBetween = (move.piece.position..move.destination).toList().drop(1).dropLast(1)

        // Can't move backwards
        if (!isDraught &&
            (move.piece.player == Player.BLACK && (move.destination - move.piece.position).y >= 0 ||
                    move.piece.player == Player.WHITE && (move.destination - move.piece.position).y <= 0)
        ) return MoveType.INVALID

        val destinationPiece = getPiece(move.destination)
        if (destinationPiece != null && !destinationPiece.eaten) return MoveType.INVALID
        if (magnitude > 2) return MoveType.INVALID
        if (magnitude == 2) {
            val pieceBetween = getPiece(pointsBetween.first()) ?: return MoveType.INVALID
            if (pieceBetween.eaten) return MoveType.INVALID
            if (pieceBetween.player == move.piece.player) return MoveType.INVALID
            return MoveType.VALID_EAT
        }
        return MoveType.VALID
    }

    public fun getPiece(position: Vector2): Piece? {
        return pieces.find { it.position == position && !it.eaten }
    }

    public fun getPieces(player: Player): List<Piece> {
        return pieces.filter { it.player == player && !it.eaten }
    }

    override fun toString(): String {
        var result = "   " + (0 until fieldSize).joinToString(separator = "  ") { it.toString() } + "\n"
        for (y in 0 until fieldSize) {
            result += "$y "
            for (x in 0 until fieldSize) {
                val piece = getPiece(Vector2(x, y))?.toString() ?: "-"
                result += " $piece "
            }
            result += "\n"
        }
        return result
    }
}