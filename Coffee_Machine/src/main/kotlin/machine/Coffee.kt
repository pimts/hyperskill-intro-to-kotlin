package machine

enum class Coffee(val water: Int, val milk: Int, val beans: Int, val price: Int) {
    ESPRESSO(water = 250, milk = 0, beans = 16, price = 4),
    LATTE(water = 350, milk = 75, beans = 20, price = 7),
    CAPPUCCINO(water = 200, milk = 100, beans = 12, price = 6),
}
