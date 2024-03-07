package cinema

class Cinema(private val rows: Int, private val seatsPerRow: Int) {
    private val totalNrOfSeats = rows * seatsPerRow
    private val seats = MutableList(rows) { MutableList(seatsPerRow) { 'S' } }
    private var currentIncome = 0

    fun printMenu() {
        println("1. Show the seats")
        println("2. Buy a ticket")
        println("3. Statistics")
        println("0. Exit")
    }

    fun printScreenRoom() {
        println("Cinema:")
        printHeader()
        printRows()
        println()
    }

    fun buyTicket() {
        val seat = askTicketChoice()
        val price = ticketPriceForSeat(seat)
        markSeatAsSold(seat)
        currentIncome += price
        println("Ticket price: $$price")
    }

    fun printStatistics() {
        println("Number of purchased tickets: ${numberOfTicketsSold()}")
        println("Percentage: ${"%.2f".format(percentageOfTicketsSold())}%")
        println("Current income: $$currentIncome")
        println("Total income: $${totalIncome()}")
    }

    private fun printHeader() {
        val header = (1..seatsPerRow).joinToString(prefix = "  ", separator = " ")
        println(header)
    }

    private fun printRows() {
        for ((index, row) in seats.withIndex()) {
            val rowString = "${index + 1} " + row.joinToString(" ")
            println(rowString)
        }
    }

    private fun askTicketChoice(): Seat {
        while (true) {
            val seat = askForSeat()
            if (isValidSeat(seat).not()) {
                println("Wrong input!")
            } else if (hasSeatBeenPurchased(seat)) {
                println("That ticket has already been purchased!")
            } else {
                return seat
            }
        }
    }

    private fun askForSeat(): Seat {
        val rowNumber = askRowNumber()
        val seatNumber = askSeatNumber()
        return Seat(rowNumber, seatNumber)
    }

    private fun askRowNumber(): Int {
        println("Enter a row number:")
        val rowNumber = readln().toInt()
        return rowNumber - 1
    }

    private fun askSeatNumber(): Int {
        println("Enter a seat number in that row:")
        val seatNumber = readln().toInt()
        return seatNumber - 1
    }

    private fun isValidSeat(seat: Seat) = seat.seatNr in 0 until seatsPerRow && seat.rowNr in 0 until rows

    private fun hasSeatBeenPurchased(seat: Seat) = seats[seat.rowNr][seat.seatNr] == 'B'

    private fun ticketPriceForSeat(seat: Seat) =
        if (isSmallRoom()) {
            TICKET_PRICE_FRONT_OR_SMALL
        } else {
            ticketPriceForSeatInLargeRoom(seat)
        }
    private fun isSmallRoom() = totalNrOfSeats <= 60

    private fun ticketPriceForSeatInLargeRoom(seat: Seat) =
        if (isSeatInFrontHalf(seat)) {
            TICKET_PRICE_FRONT_OR_SMALL
        } else {
            TICKET_PRICE_BACK
        }

    private fun isSeatInFrontHalf(seat: Seat): Boolean {
        val frontSeats = (0 until (rows / 2) * seatsPerRow)
        val seatIndex = (seat.rowNr) * seatsPerRow + seat.seatNr
        return seatIndex in frontSeats
    }

    private fun markSeatAsSold(seat: Seat) {
        seats[seat.rowNr][seat.seatNr] = 'B'
    }

    private fun numberOfTicketsSold() = seats.flatten().count { it == 'B' }

    private fun percentageOfTicketsSold() =
        numberOfTicketsSold().toDouble() / totalNrOfSeats.toDouble() * 100.0

    private fun totalIncome(): Int {
        var totalIncome = 0
        for (row in seats.indices) {
            for (seat in seats[row].indices) {
                totalIncome += ticketPriceForSeat(Seat(row, seat))
            }
        }
        return totalIncome
    }

    companion object {
        private const val TICKET_PRICE_FRONT_OR_SMALL = 10
        private const val TICKET_PRICE_BACK = 8
    }
}
