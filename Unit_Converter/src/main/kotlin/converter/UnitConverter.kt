package converter

import kotlin.IllegalStateException

enum class UnitClass { LENGTH, WEIGHT, TEMPERATURE, UNKNOWN, }

enum class Unit(
    val intermediate: Double,
    private val singular: String,
    private val plural: String,
    val unitClass: UnitClass
) {
    METER(1.0, "meter", "meters", UnitClass.LENGTH),
    KILOMETER(1000.0, "kilometer", "kilometers", UnitClass.LENGTH),
    CENTIMETER(0.01, "centimeter", "centimeters", UnitClass.LENGTH),
    MILLIMETER(0.001, "millimeter", "millimeters", UnitClass.LENGTH),
    MILE(1609.35, "mile", "miles", UnitClass.LENGTH),
    YARD(0.9144, "yard", "yards", UnitClass.LENGTH),
    FOOT(0.3048, "foot", "feet", UnitClass.LENGTH),
    INCH(0.0254, "inch", "inches", UnitClass.LENGTH),

    GRAM(1.0, "gram", "grams", UnitClass.WEIGHT),
    KILOGRAM(1000.0, "kilogram", "kilograms", UnitClass.WEIGHT),
    MILLIGRAM(0.001, "milligram", "milligrams", UnitClass.WEIGHT),
    POUND(453.592, "pound", "pounds", UnitClass.WEIGHT),
    OUNCE(28.3495, "ounce", "ounces", UnitClass.WEIGHT),

    CELSIUS(0.0, "degree Celsius", "degrees Celsius", UnitClass.TEMPERATURE),
    FAHRENHEIT(0.0, "degree Fahrenheit", "degrees Fahrenheit", UnitClass.TEMPERATURE),
    KELVIN(0.0, "kelvin", "kelvins", UnitClass.TEMPERATURE),

    UNKNOWN(-1.0, "???", "???", UnitClass.UNKNOWN);

    fun pluralize(number: Double) = if (number == 1.0) singular else plural
}

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readln().lowercase().replace("degrees ", "").replace("degree ", "")
        if (input == "exit") break

        val (numberInput, from, _, to) = input.split(" ")
        val fromUnit = getUnitFromInput(from)
        val toUnit = getUnitFromInput(to)

        try {
            val number = numberInput.toDouble()
            if (isConversionPossible(fromUnit, toUnit)) {
                convert(number, fromUnit, toUnit)
            } else {
                println("Conversion from ${fromUnit.pluralize(0.0)} to ${toUnit.pluralize(0.0)} is impossible")
            }
        } catch (e: NumberFormatException) {
            println("Parse error")
        }
        println()
    }
}

private fun isConversionPossible(fromUnit: Unit, toUnit: Unit) =
    fromUnit != Unit.UNKNOWN && toUnit != Unit.UNKNOWN && fromUnit.unitClass == toUnit.unitClass

private fun convert(number: Double, fromUnit: Unit, toUnit: Unit) = when {
    isConversionOfClass(fromUnit, toUnit, UnitClass.TEMPERATURE) -> convertTemperature(number, fromUnit, toUnit)
    isConversionOfClass(fromUnit, toUnit, UnitClass.WEIGHT) -> convertWeight(number, fromUnit, toUnit)
    isConversionOfClass(fromUnit, toUnit, UnitClass.LENGTH) -> convertLength(number, fromUnit, toUnit)
    else -> throw IllegalStateException("Unknown conversion!")
}

private fun isConversionOfClass(fromUnit: Unit, toUnit: Unit, unitClass: UnitClass) =
    fromUnit.unitClass == unitClass && toUnit.unitClass == unitClass

private fun convertWeight(number: Double, fromUnit: Unit, toUnit: Unit) {
    if (number < 0) {
        println("Weight shouldn't be negative")
        return
    }
    convertWeightOrLength(number, fromUnit, toUnit)
}

private fun convertLength(number: Double, fromUnit: Unit, toUnit: Unit) {
    if (number < 0) {
        println("Length shouldn't be negative")
        return
    }
    convertWeightOrLength(number, fromUnit, toUnit)
}

private fun convertWeightOrLength(number: Double, fromUnit: Unit, toUnit: Unit) {
    val converted = number * fromUnit.intermediate / toUnit.intermediate
    println("$number ${fromUnit.pluralize(number)} is $converted ${toUnit.pluralize(converted)}")
}

private fun convertTemperature(number: Double, fromUnit: Unit, toUnit: Unit) {
    val converted = when {
        fromUnit == Unit.CELSIUS && toUnit == Unit.FAHRENHEIT -> number * (9.0 / 5.0) + 32.0
        fromUnit == Unit.CELSIUS && toUnit == Unit.KELVIN -> number + 273.15
        fromUnit == Unit.FAHRENHEIT && toUnit == Unit.CELSIUS -> (number - 32.0) * 5.0 / 9.0
        fromUnit == Unit.FAHRENHEIT && toUnit == Unit.KELVIN -> (number + 459.67) * 5.0 / 9.0
        fromUnit == Unit.KELVIN && toUnit == Unit.CELSIUS -> number - 273.15
        fromUnit == Unit.KELVIN && toUnit == Unit.FAHRENHEIT -> number * 9.0 / 5.0 - 459.67
        fromUnit == Unit.CELSIUS && toUnit == Unit.CELSIUS
                || fromUnit == Unit.FAHRENHEIT && toUnit == Unit.FAHRENHEIT
                || fromUnit == Unit.KELVIN && toUnit == Unit.KELVIN -> number

        else -> throw IllegalStateException("Unknown conversion!")
    }
    println("$number ${fromUnit.pluralize(number)} is $converted ${toUnit.pluralize(converted)}")
}

private fun getUnitFromInput(inputUnit: String) = when (inputUnit) {
    "m", "meter", "meters" -> Unit.METER
    "km", "kilometer", "kilometers" -> Unit.KILOMETER
    "cm", "centimeter", "centimeters" -> Unit.CENTIMETER
    "mm", "millimeter", "millimeters" -> Unit.MILLIMETER
    "mi", "mile", "miles" -> Unit.MILE
    "yd", "yard", "yards" -> Unit.YARD
    "ft", "foot", "feet" -> Unit.FOOT
    "in", "inch", "inches" -> Unit.INCH
    "g", "gram", "grams" -> Unit.GRAM
    "kg", "kilogram", "kilograms" -> Unit.KILOGRAM
    "mg", "milligram", "milligrams" -> Unit.MILLIGRAM
    "lb", "pound", "pounds" -> Unit.POUND
    "oz", "ounce", "ounces" -> Unit.OUNCE
    "degree Celsius", "degrees Celsius", "celsius", "dc", "c" -> Unit.CELSIUS
    "degree Fahrenheit", "degrees Fahrenheit", "fahrenheit", "df", "f" -> Unit.FAHRENHEIT
    "kelvin", "kelvins", "k" -> Unit.KELVIN
    else -> Unit.UNKNOWN
}