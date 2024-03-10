package minesweeper

import kotlin.random.Random

class Minesweeper(private val rows: Int, private val columns: Int, private val mines: Int) {
    private val board = MutableList(rows) { y -> MutableList<Cell>(columns) { x -> Cell.Safe(x, y) } }
    private var state = GameState.RUNNING
    private var lastExploredCell: Cell? = null

    fun performMove(x: Int, y: Int, move: String) {
        when (move) {
            "free" -> {
                if (isFirstExploreMove()) {
                    placeMinesAndHints(x, y)
                }
                explore(x, y)
            }

            "mine" -> mark(x, y)
            else -> throw IllegalStateException("Unknown move entered!")
        }
        updateGameState()
    }

    private fun isFirstExploreMove() = mineCount() == 0

    private fun mineCount() = board.flatten().count { it is Cell.Mine }

    private fun placeMinesAndHints(firstMoveX: Int, firstMoveY: Int) {
        while (mineCount() < mines) {
            placeMineAndHints(firstMoveX, firstMoveY)
        }
    }

    private fun placeMineAndHints(firstMoveX: Int, firstMoveY: Int) {
        val mine = getNewMine(firstMoveX, firstMoveY)
        board[mine.y][mine.x] = mine
        updateSurroundingSafeCells(mine.x, mine.y)
    }

    private fun updateSurroundingSafeCells(x: Int, y: Int) {
        val surroundingCells = getSurroundingSafeCells(x, y)
        surroundingCells.forEach {
            it.surroundingMines++
        }
    }

    private fun getNewMine(firstMoveX: Int, firstMoveY: Int): Cell.Mine {
        var mine: Cell.Mine
        do {
            val y = Random.nextInt(rows)
            val x = Random.nextInt(columns)
            mine = Cell.Mine(x, y)
            mine.isMarked = board[y][x].isMarked
        } while (board[y][x] is Cell.Mine || y == firstMoveY && x == firstMoveX)
        return mine
    }

    private fun explore(x: Int, y: Int) {
        val cell = board[y][x]
        when (cell) {
            is Cell.Mine -> gameOver()
            is Cell.Safe -> {
                exploreCell(cell)
            }
        }
        lastExploredCell = cell
    }

    private fun exploreCell(cell: Cell.Safe) {
        if (cell.isExplored) {
            return
        }

        cell.isExplored = true
        cell.isMarked = false

        if (cell.surroundingMines == 0) {
            val surroundingCells = getSurroundingSafeCells(cell.x, cell.y)
            for (surroundingCell in surroundingCells) {
                exploreCell(surroundingCell)
            }
        }
    }

    private fun getSurroundingSafeCells(x: Int, y: Int): List<Cell.Safe> {
        val cells = mutableListOf<Cell.Safe>()
        for (yShift in -1..1) {
            for (xShift in -1..1) {
                val surroundingX = x + xShift
                val surroundingY = y + yShift
                if (isValidXCoord(surroundingX) && isValidYCoord(surroundingY) && !isCenterCell(xShift, yShift)) {
                    val surroundingCell = board[surroundingY][surroundingX]
                    if (surroundingCell is Cell.Safe) {
                        cells.add(surroundingCell)
                    }
                }
            }
        }
        return cells.toList()
    }

    private fun isValidXCoord(x: Int) = x in 0 until columns

    private fun isValidYCoord(y: Int) = y in 0 until rows

    private fun isCenterCell(xShift: Int, yShift: Int) = xShift == 0 && yShift == 0

    private fun gameOver() {
        board.flatten().filterIsInstance<Cell.Mine>().forEach { it.isHidden = false }
        state = GameState.LOST
    }

    private fun mark(x: Int, y: Int) {
        board[y][x].isMarked = !board[y][x].isMarked
    }

    private fun updateGameState() = when {
        allSafeCellsExplored() || allMinesNoSafeMarked() -> state = GameState.WON
        lastExploredCell is Cell.Mine -> state = GameState.LOST
        else -> state = GameState.RUNNING
    }

    private fun allMinesNoSafeMarked() = allMinesMarked() && noSafeCellsMarked()

    private fun allSafeCellsExplored() =
        board.flatten()
            .filterIsInstance<Cell.Safe>()
            .all { it.isExplored }

    private fun allMinesMarked() =
        board.flatten()
            .filterIsInstance<Cell.Mine>()
            .all { it.isMarked }

    private fun noSafeCellsMarked() =
        board.flatten()
            .filterIsInstance<Cell.Safe>()
            .none { it.isMarked }

    fun print() {
        printHeader()
        printRows()
        printFooter()
    }

    private fun printHeader() {
        val header = (1..columns).joinToString(prefix = " |", separator = "", postfix = "|")
        println(header)
        println("-|${"-".repeat(columns)}|")
    }

    private fun printRows() {
        for (row in board.indices) {
            println(board[row].joinToString(prefix = "${row + 1}|", separator = "", postfix = "|"))
        }
    }

    private fun printFooter() {
        println("-|${"-".repeat(columns)}|")
    }

    fun getGameState() = state
}
