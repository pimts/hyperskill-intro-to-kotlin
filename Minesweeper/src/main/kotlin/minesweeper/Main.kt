package minesweeper

fun main() {
    print("How many mines do you want on the field? ")
    val mines = readln().toInt()

    val minefield = Minesweeper(9, 9, mines)
    minefield.print()

    var gameOver = false
    while (gameOver.not()) {
        when (minefield.getGameState()) {
            GameState.WON -> {
                println("Congratulations! You found all the mines!")
                gameOver = true
            }

            GameState.LOST -> {
                println("You stepped on a mine and failed!")
                gameOver = true
            }

            GameState.RUNNING -> {
                print("Set/unset mine marks or claim a cell as free:")
                val (xInput, yInput, move) = readln().split(" ")
                val x = xInput.toInt() - 1
                val y = yInput.toInt() - 1
                minefield.performMove(x, y, move)
                minefield.print()
            }
        }
    }
}