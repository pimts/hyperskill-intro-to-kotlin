package tictactoe

import java.lang.IllegalStateException
import kotlin.math.abs

const val GRID_NUM_ROWS = 3
const val GRID_NUM_COLUMNS = 3
val GRID = List(GRID_NUM_ROWS) {
    MutableList(GRID_NUM_COLUMNS) { ' ' }
    MutableList(GRID_NUM_COLUMNS) { ' ' }
    MutableList(GRID_NUM_COLUMNS) { ' ' }
}

fun main() {
    var currentPlayer = 'X'
    printGameGrid()
    while (getGameStateFromGrid() == "Game not finished") {
        val (x, y) = getUserMove()
        markCellInGrid(currentPlayer, x, y)
        currentPlayer = getNextPlayer(currentPlayer)
        printGameGrid()
    }
    println(getGameStateFromGrid())
}

private fun getGameStateFromGrid(): String {
    return when {
        isImpossible() -> "Impossible"
        isGameNotFinished() -> "Game not finished"
        isDraw() -> "Draw"
        hasPlayerWon('X') -> "X wins"
        hasPlayerWon('O') -> "O wins"
        else -> throw IllegalStateException("No such game state defined!")
    }
}

private fun isImpossible(): Boolean {
    val numberOfX = countCellsForPlayer('X')
    val numberOfO = countCellsForPlayer('O')
    val difference = abs(numberOfX - numberOfO)
    return hasPlayerWon('X') && hasPlayerWon('O') || difference >= 2
}

private fun countCellsForPlayer(player: Char) =
    GRID.flatten().count { it == player }

private fun isGameNotFinished() =
    hasPlayerWon('O').not() && hasPlayerWon('X').not() && hasEmptyCells()

private fun isDraw() =
    hasPlayerWon('O').not() && hasPlayerWon('X').not() && hasEmptyCells().not()

private fun hasPlayerWon(player: Char) =
    hasHorizontalRow(player) || hasVerticalRow(player) || hasDiagonalRow(player)

private fun hasHorizontalRow(player: Char): Boolean {
    for (row in GRID) {
        if (row.all { it == player }) {
            return true
        }
    }
    return false
}

private fun hasVerticalRow(player: Char): Boolean {
    for (column in 0 until GRID_NUM_COLUMNS) {
        if (GRID[0][column] == player && GRID[1][column] == player && GRID[2][column] == player) {
            return true
        }
    }
    return false
}

private fun hasDiagonalRow(player: Char) =
    hasDiagonalRowBottomToTop(player) || hasDiagonalRowTopToBottom(player)

private fun hasDiagonalRowTopToBottom(player: Char) =
    GRID[0][0] == player && GRID[1][1] == player && GRID[2][2] == player

private fun hasDiagonalRowBottomToTop(player: Char) =
    GRID[0][2] == player && GRID[1][1] == player && GRID[2][0] == player

private fun hasEmptyCells(): Boolean {
    for (y in 1..GRID_NUM_ROWS) {
        for (x in 1..GRID_NUM_COLUMNS) {
            if (isCellEmpty(x, y)) {
                return true
            }
        }
    }
    return false
}

private fun getUserMove(): List<Int> {
    var input = readln()
    while (isValidMoveInput(input).not()) {
        input = readln()
    }
    return input.split(' ').map { it.toInt() - 1 }
}

private fun isValidMoveInput(input: String) =
    when {
        isNumericInput(input).not() -> {
            println("You should enter numbers!")
            false
        }

        isValidXCoordinate(input[0].digitToInt()).not() || isValidYCoordinate(input[2].digitToInt()).not() -> {
            println("Coordinates should be from 1 to 3!")
            false
        }

        isCellEmpty(input[0].digitToInt(), input[2].digitToInt()).not() -> {
            println("This cell is occupied! Choose another one!")
            false
        }

        else -> true
    }

private fun isNumericInput(input: String) =
    try {
        input.split(" ").map { it.toInt() }
        true
    } catch (e: NumberFormatException) {
        false
    }

private fun isValidXCoordinate(coordinate: Int) = coordinate in 1..GRID_NUM_COLUMNS

private fun isValidYCoordinate(coordinate: Int) = coordinate in 1..GRID_NUM_ROWS

private fun isCellEmpty(x: Int, y: Int) = GRID[x - 1][y - 1] == ' '

private fun markCellInGrid(player: Char, x: Int, y: Int) {
    GRID[x][y] = player
}

private fun printGameGrid() {
    printTopBorder()
    for (row in GRID) {
        printRowWithBorders(row)
    }
    printBottomBorder()
}

private fun printTopBorder() = println("---------")

private fun printRowWithBorders(rowString: List<Char>) {
    val rowWithBorders = rowString.joinToString(prefix = "| ", separator = " ", postfix = " |")
    println(rowWithBorders)
}

private fun printBottomBorder() = println("---------")

private fun getNextPlayer(currentPlayer: Char) =
    if (currentPlayer == 'X') {
        'O'
    } else {
        'X'
    }
