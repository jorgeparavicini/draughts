package com.jorgeparavicini.draughts.model.enums

import com.jorgeparavicini.draughts.model.core.Vector2

public enum class Direction(public val direction: Vector2) {
    TOP_RIGHT(Vector2(1, 1)),
    BOTTOM_RIGHT(Vector2(1, -1)),
    BOTTOM_LEFT(Vector2(-1, -1)),
    TOP_LEFT(Vector2(-1, 1)),
    NONE(Vector2.zero());
}