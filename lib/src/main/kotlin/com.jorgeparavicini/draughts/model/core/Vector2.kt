package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.model.enums.Direction
import kotlin.math.abs

public data class Vector2(val x: Int, val y: Int) : Comparable<Vector2>, Iterable<Vector2> {

    val diagonalMagnitude: Int
        get() {
            if (!isDiagonal) throw IllegalStateException("The Vector does not represent a diagonal direction")
            return abs(x)
        }

    val isDiagonal: Boolean
        get() = abs.x == abs.y

    val direction: Direction
        get() {
            if (!isDiagonal) throw IllegalStateException("Only diagonal directions are supported")
            if (x == 0) return Direction.NONE
            return if (x < 0) {
                if (y < 0) Direction.BOTTOM_LEFT else Direction.TOP_LEFT
            } else {
                if (y < 0) Direction.BOTTOM_RIGHT else Direction.TOP_RIGHT
            }
        }

    val abs: Vector2
        get() = Vector2(abs(x), abs(y))

    val max: Int
        get() = kotlin.math.max(x, y)

    val min: Int
        get() = kotlin.math.min(x, y)

    public operator fun plus(other: Vector2): Vector2 {
        return Vector2(x + other.x, y + other.y)
    }

    public operator fun minus(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    public operator fun times(scalar: Int): Vector2 {
        return Vector2(x * scalar, y * scalar)
    }

    public operator fun rangeTo(other: Vector2): Vector2Range = Vector2Range(this, other)

    override fun compareTo(other: Vector2): Int {
        return diagonalMagnitude.compareTo(other.diagonalMagnitude)
    }

    override fun iterator(): Iterator<Vector2> {
        return Vector2Iterator(this, zero())
    }

    public fun getDiagonalPositions(bounds: Int): Set<Vector2> {
        val exclusiveBounds = bounds - 1
        val q1Min = kotlin.math.min(exclusiveBounds - x, exclusiveBounds - y)
        val q2Min = kotlin.math.min(x, exclusiveBounds - y)
        val q3Min = kotlin.math.min(x, y)
        val q4Min = kotlin.math.min(exclusiveBounds - x, y)

        return (this..Vector2(x + q1Min, y + q1Min)).toSet().union(
            this..Vector2(x - q2Min, y + q2Min)
        ).union(
            this..Vector2(x - q3Min, y - q3Min)
        ).union(
            this..Vector2(x + q4Min, y - q4Min)
        )
    }

    public inner class Vector2Range(
        override val start: Vector2, override val endInclusive: Vector2
    ) : ClosedRange<Vector2>, Iterable<Vector2> {
        override fun iterator(): Iterator<Vector2> {
            return Vector2Iterator(start, endInclusive)
        }
    }

    public inner class Vector2Iterator(
        private var value: Vector2, private val endInclusive: Vector2
    ) : Iterator<Vector2> {
        private val initialDirection = (endInclusive - value).direction

        override fun hasNext(): Boolean {
            if (initialDirection == Direction.NONE) return false
            return (endInclusive - value).direction in listOf(initialDirection, Direction.NONE)
        }

        override fun next(): Vector2 {
            val old = value
            value = Vector2(
                value.x + initialDirection.direction.x, value.y + initialDirection.direction.y
            )
            return old
        }
    }

    public companion object {
        public fun zero(): Vector2 = Vector2(0, 0)

        public fun one(): Vector2 = Vector2(1, 1)
    }
}