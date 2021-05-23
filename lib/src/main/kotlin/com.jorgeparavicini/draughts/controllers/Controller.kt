package com.jorgeparavicini.draughts.controllers

import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.enums.Player

abstract class Controller {
    lateinit var player: Player
        internal set

    abstract suspend fun getMove(): Move

    open fun illegalMove(move: Move, message: String?) {}
}