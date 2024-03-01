package machine

fun main() {
    val coffeeMachine = CoffeeMachine()
    while (coffeeMachine.isSwitchedOn()) {
        val input = readln()
        coffeeMachine.handleInput(input)
    }
}
