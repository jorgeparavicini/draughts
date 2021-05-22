package com.jorgeparavicini.draughts.model.enums

enum class Player(private val displayValue: String) {
    BLACK("□"),
    WHITE("■");

    override fun toString(): String {
        return displayValue
    }
}