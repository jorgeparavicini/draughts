package com.jorgeparavicini.draughts.model.core

import com.jorgeparavicini.draughts.controllers.Controller
import com.jorgeparavicini.draughts.model.enums.FieldSize
import com.jorgeparavicini.draughts.model.enums.Player
import com.jorgeparavicini.draughts.model.exceptions.IllegalMoveException
import mu.KotlinLogging

private val logger = KotlinLogging.logger {  }

class Draughts(
    private val blackController: Controller,
    private val whiteController: Controller,
    size: FieldSize = FieldSize.SIZE_8x8
) {
    val field = Field(size)

    val winner: Player?
        get() = this.field.winner

    val isGameOver: Boolean
        get() = this.field.isGameOver

    var hasStarted: Boolean = false
        private set

    var turn: Int = 0
        private set

    val currentPlayer: Player
        get() = if (turn % 2 == 0) Player.BLACK else Player.WHITE

    val currentController: Controller
        get() = if (currentPlayer == Player.BLACK) blackController else whiteController

    fun reset() {
        turn = 0
        field.reset()
        hasStarted = false
    }

    fun startGame() {
        if (hasStarted) throw IllegalStateException("Game already started")
        hasStarted = true
    }

    suspend fun nextTurn() {
        if (isGameOver) throw IllegalStateException("Game is already over")
        playTurn(currentController)
        turn += 1
    }

    private suspend fun playTurn(controller: Controller) {
        if (isGameOver) throw IllegalStateException("Game is already over")

        while (true) {
            if (isGameOver) break
            val move = controller.getMove(currentPlayer)
            try {
                if (!field.executeMove(move, currentPlayer)) break
            } catch (e: IllegalMoveException) {

                logger.trace("Move was not allowed: $move")
                controller.illegalMove(move, e.message)
            }
        }
    }
}