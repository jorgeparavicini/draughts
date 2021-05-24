package com.jorgeparavicini.draughts.controllers

import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.enums.Player

public abstract class Controller {
    public lateinit var player: Player
        internal set

    public abstract suspend fun getMove(): Move

    public open fun illegalMove(move: Move, message: String?) {}
}