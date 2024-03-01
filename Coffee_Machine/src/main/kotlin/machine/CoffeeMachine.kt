package machine

class CoffeeMachine(
    private var water: Int = 400,
    private var milk: Int = 540,
    private var coffeeBeans: Int = 120,
    private var cups: Int = 9,
    private var cash: Int = 550,
) {
    private var state = MachineState.SELECTING_ACTION
    private var refillState = RefillState.WATER

    init {
        updatePrompt()
    }

    fun handleInput(input: String) {
        state = when (state) {
            MachineState.SELECTING_ACTION -> handleAction(input)
            MachineState.CHOOSING_COFFEE -> handleCoffeeChoice(input)
            MachineState.FILLING -> handleRefill(input)
            MachineState.SHUTDOWN -> MachineState.SHUTDOWN
        }
        updatePrompt()
    }

    private fun updatePrompt() {
        when (state) {
            MachineState.SELECTING_ACTION -> println("Write action (buy, fill, take, remaining, exit):")
            MachineState.CHOOSING_COFFEE -> println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu:")
            MachineState.FILLING ->
                when (refillState) {
                    RefillState.WATER -> println("Write how many ml of water you want to add:")
                    RefillState.MILK -> println("Write how many ml of milk you want to add:")
                    RefillState.COFFEE_BEANS -> println("Write how many grams of beans you want to add:")
                    RefillState.CUPS -> println("Write how many disposable cups you want to add:")
                }

            MachineState.SHUTDOWN -> Unit
        }
    }

    private fun handleAction(action: String): MachineState =
        when (Action.valueOf(action.uppercase())) {
            Action.BUY -> MachineState.CHOOSING_COFFEE
            Action.FILL -> MachineState.FILLING
            Action.TAKE -> {
                takeCash()
                MachineState.SELECTING_ACTION
            }

            Action.REMAINING -> {
                printMachineState()
                MachineState.SELECTING_ACTION
            }

            Action.EXIT -> MachineState.SHUTDOWN
        }

    private fun takeCash() {
        println("I gave you $$cash")
        println()
        cash = 0
    }

    private fun printMachineState() = println(
        """
        The coffee machine has:
        $water ml of water
        $milk ml of milk
        $coffeeBeans g of coffee beans
        $cups disposable cups
        $$cash of money
        
    """.trimIndent()
    )

    private fun handleCoffeeChoice(coffeeInput: String): MachineState {
        when (coffeeInput) {
            "1" -> makeCoffee(Coffee.ESPRESSO)
            "2" -> makeCoffee(Coffee.LATTE)
            "3" -> makeCoffee(Coffee.CAPPUCCINO)
            "back" -> Unit
        }
        return MachineState.SELECTING_ACTION
    }

    private fun makeCoffee(coffee: Coffee) {
        if (hasEnoughResources(coffee)) {
            println("I have enough resources, making you a coffee!\n")
            updateResources(coffee)
        }
    }

    private fun hasEnoughResources(coffee: Coffee) = when {
        water < coffee.water -> {
            println("Sorry, not enough water!\n")
            false
        }

        milk < coffee.milk -> {
            println("Sorry, not enough milk!\n")
            false
        }

        coffeeBeans < coffee.beans -> {
            println("Sorry, not enough coffee beans!\n")
            false
        }

        cups <= 0 -> {
            println("Sorry, not enough disposable cups!\n")
            false
        }

        else -> {
            true
        }
    }

    private fun updateResources(coffee: Coffee) {
        water -= coffee.water
        milk -= coffee.milk
        coffeeBeans -= coffee.beans
        cups -= 1
        cash += coffee.price
    }

    private fun handleRefill(input: String): MachineState {
        refillMachine(input)
        updateRefillState()

        return if (refillState == RefillState.WATER) {
            MachineState.SELECTING_ACTION
        } else {
            MachineState.FILLING
        }
    }

    private fun refillMachine(input: String) =
        when (refillState) {
            RefillState.WATER -> refillWater(input)
            RefillState.MILK -> refillMilk(input)
            RefillState.COFFEE_BEANS -> refillCoffeeBeans(input)
            RefillState.CUPS -> refillCups(input)
        }

    private fun updateRefillState() {
        refillState = when (refillState) {
            RefillState.WATER -> RefillState.MILK
            RefillState.MILK -> RefillState.COFFEE_BEANS
            RefillState.COFFEE_BEANS -> RefillState.CUPS
            RefillState.CUPS -> RefillState.WATER
        }
    }

    private fun refillWater(input: String) {
        water += input.toInt()
    }

    private fun refillMilk(input: String) {
        milk += input.toInt()
    }

    private fun refillCoffeeBeans(input: String) {
        coffeeBeans += input.toInt()
    }

    private fun refillCups(input: String) {
        cups += input.toInt()
    }

    fun isSwitchedOn() = state != MachineState.SHUTDOWN
}
