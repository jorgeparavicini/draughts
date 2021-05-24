package com.jorgeparavicini.draughts.model.enums

public enum class Player(private val displayValue: String, public val icon: String) {
    BLACK("Black", "□"),
    WHITE("White", "■");

    override fun toString(): String {
        return displayValue
    }
}