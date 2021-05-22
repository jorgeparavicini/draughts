package com.jorgeparavicini.draughts.controllers

import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.enums.Player

abstract class Controller {
    abstract suspend fun getMove(player: Player): Move

    abstract fun illegalMove(move: Move, message: String?)
}