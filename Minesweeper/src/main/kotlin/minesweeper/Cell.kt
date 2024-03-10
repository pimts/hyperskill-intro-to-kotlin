package minesweeper

sealed class Cell(val x: Int, val y: Int, var isMarked: Boolean = false) {
    class Mine(x: Int, y: Int, var isHidden: Boolean = true) : Cell(x, y) {
        override fun toString() =
            when {
                isMarked -> MARKED
                isHidden.not() -> MINE
                else -> UNEXPLORED
            }
    }

    class Safe(x: Int, y: Int, var surroundingMines: Int = 0, var isExplored: Boolean = false) : Cell(x, y) {
        override fun toString() = when {
            isMarked -> MARKED
            isExplored && surroundingMines == 0 -> FREE
            isExplored && surroundingMines > 0 -> surroundingMines.toString()
            else -> UNEXPLORED
        }
    }

    companion object {
        private const val MARKED = "*"
        private const val FREE = "/"
        private const val UNEXPLORED = "."
        private const val MINE = "X"
    }
}
