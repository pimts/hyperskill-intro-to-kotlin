package cinema

fun main() {
    println("Enter the number of rows:")
    val rows = readln().toInt()

    println("Enter the number of seats in each row:")
    val seatsPerRow = readln().toInt()

    val cinema = Cinema(rows, seatsPerRow)

    while (true) {
        cinema.printMenu()
        val input = readln()
        when (input) {
            "1" -> cinema.printScreenRoom()
            "2" -> cinema.buyTicket()
            "3" -> cinema.printStatistics()
            "0" -> break
        }
    }
}