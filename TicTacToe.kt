package com.jetbrains.gio

const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[90m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_PURPLE = "\u001B[35m"
const val ANSI_CYAN = "\u001B[36m"
const val ANSI_BOLD = "\u001B[1m"

var player1: String = "Player"
var player2: String = "Computer"
var playgroundMatrix = arrayOf<Int>(0, 0, 0, 0, 0, 0, 0, 0, 0)

class Messages {
    fun welcome(): Unit = println(ANSI_BOLD + "Welcome Player/s to" + ANSI_RESET)

    fun logo(): Unit = println(
        ANSI_PURPLE +
                "_______ _   _______      _______         \n" +
                "|__   __(_) |__   __|    |__   __|        \n" +
                "   | |   _  ___| | __ _  ___| | ___   ___ \n" +
                "   | |  | |/ __| |/ _` |/ __| |/ _ \\ / _ \\\n" +
                "   | |  | | (__| | (_| | (__| | (_) |  __/\n" +
                "   |_|  |_|\\___|_|\\__,_|\\___|_|\\___/ \\___|" +
                ANSI_RESET
    )

    private fun enterNumber(): String = ANSI_PURPLE + "Enter number: " + ANSI_RESET

    fun turnMessage(playerNumber: Int): Unit = print(
        "It's ${playerName(playerNumber)}'s turn..\n" +
                enterNumber()
    )

    fun blankLink(): Unit = println()

    fun playModeSelection(): Unit =
        print(
            "How do you want to play?\n" +
                    ANSI_BOLD + "\t[1]" + ANSI_RESET + ": ${PlayModeEnum.PvP} - Player vs. Player \n" +
                    ANSI_BOLD + ANSI_BLACK + "\t[2]" + ANSI_RESET + ANSI_BLACK + ": ${PlayModeEnum.PvC} - Player vs. Computer (not implemented)\r\n\r\n" + ANSI_RESET +
                    enterNumber()
        )

    fun setPlayerName(playerNumber: Int): Unit = print("Set name for player $playerNumber: ")

    fun startGame(): Unit = println(ANSI_PURPLE + "Starting game" + ".".repeat(3) + ANSI_RESET + "\r\n")

    private fun playerName(playerNumber: Int): String = when (playerNumber) {
        1 -> ANSI_BOLD + ANSI_CYAN + Player().getPlayerName(playerNumber) + ANSI_RESET
        2 -> ANSI_BOLD + ANSI_PURPLE + Player().getPlayerName(playerNumber) + ANSI_RESET
        else -> ""
    }

    fun playerHasWon(playerNumber: Int): Unit =
        println("Congratulation ${playerName(playerNumber)}, you has won this match.")
}

class Playground {
    fun drawPlayground() {
        Messages().blankLink()
        println("  ${setSign(1)}  |  ${setSign(2)}  |  ${setSign(3)}  ")
        println(" --- + --- + --- ")
        println("  ${setSign(4)}  |  ${setSign(5)}  |  ${setSign(6)}  ")
        println(" --- + --- + --- ")
        println("  ${setSign(7)}  |  ${setSign(8)}  |  ${setSign(9)}  ")
    }

    private fun setSign(fieldNumber: Int): String =
        when (playgroundMatrix[fieldNumber - 1]) {
            1 -> ANSI_CYAN + "x" + ANSI_RESET
            2 -> ANSI_PURPLE + "o" + ANSI_RESET
            else -> ANSI_BLACK + fieldNumber + ANSI_RESET
        }
}

enum class PlayModeEnum {
    PvP,
    PvC
}

class PlayMode {
    var selectedPlayMode: PlayModeEnum = PlayModeEnum.PvP
    private var isSelected: Boolean = false
    private var messages = Messages()

    fun selectPlayMode() {
        do {
            messages.playModeSelection()
            setPlayMode(readLine()!!)
            messages.blankLink()
        } while (!isSelected)
    }

    private fun setPlayMode(playMode: String) {
        when (playMode) {
            "1" -> {
                selectedPlayMode = PlayModeEnum.PvP
                isSelected = true
            }
            "2" -> {
                selectedPlayMode = PlayModeEnum.PvC
                isSelected = true
            }
            else -> {
                isSelected = false
            }

        }
    }
}

class Player {
    private var currentTurn: Int = 1
    var lastTurn: Int = 1
    private var alreadySet = false
    private val messages = Messages()

    fun setPlayerName(playerNumber: Int) {
        Messages().setPlayerName(playerNumber)
        val input = readLine()!!
        when (playerNumber) {
            1 -> player1 = input
            2 -> player2 = input
        }
    }

    fun getPlayerName(playerNumber: Int): String = when (playerNumber) {
        1 -> player1
        2 -> player2
        else -> "unkown player"
    }

    fun turn() {
        messages.blankLink()
        messages.turnMessage(currentTurn)

        val turnField = readLine()!!.toInt()

        do {
            when (playgroundMatrix[turnField - 1] == 0) {
                true -> {
                    alreadySet = true
                    when (turnField) {
                        in 1..9 -> playgroundMatrix[turnField - 1] = currentTurn
                    }
                    when (currentTurn) {
                        1 -> {
                            currentTurn = 2
                            lastTurn = 1
                        }
                        2 -> {
                            currentTurn = 1
                            lastTurn = 2
                        }
                    }
                }
                false -> println(ANSI_RED + "Was already set, choose another field" + ANSI_RESET)
            }
        } while (!alreadySet)
    }
}

class Game {
    private val player = Player()
    private val playground = Playground()
    private val messages = Messages()
    private var gameEndReached = false

    fun start() {
        playground.drawPlayground()
        do {
            player.turn()
            playground.drawPlayground()
            messages.blankLink()
            end()
        } while (!gameEndReached)
    }

    private fun checkRowInMatrix(matrix: Array<Int>, first: Int, second: Int, third: Int): Boolean =
        (matrix[first - 1] == matrix[second - 1] && matrix[third - 1] == matrix[first - 1] && matrix[first - 1] != 0)

    private fun end() {
        val matrix = playgroundMatrix
        when (
            matrix.all { it != 0 } ||
                    checkRowInMatrix(matrix, 1, 2, 3) ||
                    checkRowInMatrix(matrix, 4, 5, 6) ||
                    checkRowInMatrix(matrix, 7, 8, 9) ||
                    checkRowInMatrix(matrix, 1, 4, 7) ||
                    checkRowInMatrix(matrix, 2, 5, 8) ||
                    checkRowInMatrix(matrix, 3, 6, 9) ||
                    checkRowInMatrix(matrix, 1, 5, 9) ||
                    checkRowInMatrix(matrix, 3, 5, 7)
            ) {
            true -> {
                gameEndReached = true
                println("Game is over. Player ${player.getPlayerName(player.lastTurn)} wins")
                messages.playerHasWon(player.lastTurn)
            }
        }

    }
}

fun main() {
    val messages = Messages()
    val player = Player()
    val playMode = PlayMode()
    val game = Game()

    messages.welcome()
    messages.logo()
    messages.blankLink()

    playMode.selectPlayMode()

    player.setPlayerName(1)
    if (playMode.selectedPlayMode == PlayModeEnum.PvP) {
        player.setPlayerName(2)
    }
    messages.blankLink()

    messages.startGame()
    game.start()
}