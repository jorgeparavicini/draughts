package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.controllers.Controller
import com.jorgeparavicini.draughts.model.enums.FieldSize
import com.jorgeparavicini.draughts.model.enums.Player
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }
public typealias MoveExecutedHandler = (from: Vector2, to: Vector2, player: Player, didEat: Boolean) -> Unit

public class Draughts(
    private val blackController: Controller,
    private val whiteController: Controller,
    size: FieldSize = FieldSize.SMALL,
    initialMoves: List<Move>? = null
) {
    public val field: Field = Field(size)

    public val winner: Player?
        get() = this.field.winner

    public val isGameOver: Boolean
        get() = this.field.isGameOver

    public var turn: Int = 0
        private set

    public val currentPlayer: Player
        get() = if (turn % 2 == 0) Player.BLACK else Player.WHITE

    public val currentController: Controller
        get() = if (currentPlayer == Player.BLACK) blackController else whiteController

    private var onMoveExecutedHandler: MoveExecutedHandler? = null

    init {
        blackController.player = Player.BLACK
        whiteController.player = Player.WHITE

        initialMoves?.forEach { move ->
            nextMove(move)
        }
    }

    public fun reset() {
        turn = 0
        field.reset()
    }

    public fun setOnMoveExecutedHandler(handler: MoveExecutedHandler) {
        onMoveExecutedHandler = handler
    }

    public suspend fun nextTurn() {
        if (isGameOver) throw IllegalStateException("Game is already over")
        val move = currentController.getMove()
        val from = move.piece.position
        val didEat = field.executeMove(move, currentPlayer)
        onMoveExecutedHandler?.invoke(from, move.destination, move.piece.player, didEat)

        if (!didEat) {
            turn += 1
        }
    }

    public fun nextMove(move: Move) {
        if (isGameOver) throw IllegalStateException("Game is already over")
        if (!field.executeMove(move, currentPlayer)) {
            turn += 1
        }
    }
}