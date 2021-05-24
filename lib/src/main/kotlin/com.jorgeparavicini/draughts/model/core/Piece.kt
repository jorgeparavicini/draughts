package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.model.enums.Player

public typealias PieceMovedHandler = (Vector2, Vector2) -> Unit
public typealias PieceEatenHandler = () -> Unit
public typealias PiecePromotedListener = () -> Unit

public open class Piece(
    public val player: Player,
    private val initialPosition: Vector2
) {
    public var isDraught: Boolean = false
        internal set(value) {
            if (field == value) return
            field = value
            if (field) {
                onPiecePromoted?.invoke()
            }
        }

    public var position: Vector2 = initialPosition
        internal set(value) {
            val old = field
            field = value
            if (old != value) {
                onPieceMoved?.invoke(old, value)
            }
        }

    public var eaten: Boolean = false
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

    public fun setOnPieceMovedHandler(handler: PieceMovedHandler) {
        onPieceMoved = handler
    }

    public fun setOnPieceEatenHandler(handler: PieceEatenHandler) {
        onPieceEaten = handler
    }

    public fun setOnPiecePromotedHandler(handler: PiecePromotedListener) {
        onPiecePromoted = handler
    }

    internal fun reset() {
        eaten = false
        isDraught = false
        position = initialPosition
    }

    override fun toString(): String {
        return player.icon
    }
}