package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.model.enums.Player

typealias PieceMovedHandler = (Vector2, Vector2) -> Unit
typealias PieceEatenHandler = () -> Unit
typealias PiecePromotedListener = () -> Unit

open class Piece(
    val player: Player,
    private val initialPosition: Vector2
) {
    var isDraught: Boolean = false
        internal set(value) {
            if (field == value) return
            field = value
            if (field) {
                onPiecePromoted?.invoke()
            }
        }

    var position: Vector2 = initialPosition
        internal set(value) {
            val old = field
            field = value
            if (old != value) {
                onPieceMoved?.invoke(old, value)
            }
        }

    var eaten: Boolean = false
        internal set(value) {
            if (field == value) return
            field = value
            if (field) {
                onPieceEaten?.invoke()
            }
        }

    private var onPieceMoved: PieceMovedHandler? = null

    private var onPieceEaten: PieceEatenHandler? = null

    private var onPiecePromoted: PiecePromotedListener? = null

    fun setOnPieceMovedHandler(handler: PieceMovedHandler) {
        onPieceMoved = handler
    }

    fun setOnPieceEatenHandler(handler: PieceEatenHandler) {
        onPieceEaten = handler
    }

    fun setOnPiecePromotedHandler(handler: PiecePromotedListener) {
        onPiecePromoted = handler
    }

    internal fun reset() {
        eaten = false
        isDraught = false
        position = initialPosition
    }

    override fun toString(): String {
        return player.toString()
    }
}