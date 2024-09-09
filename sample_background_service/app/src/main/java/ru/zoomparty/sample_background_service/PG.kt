package ru.zoomparty.sample_background_service

fun main() {
    val phoneNumbers = getNumbers()

    println("Номера телефонов, начинающиеся с +7:")
    val filteredNumbers = phoneNumbers.filter { it.startsWith("+7") }
    filteredNumbers.forEach { println(it) }

    val uniqueNumbers = phoneNumbers.toSet()
    println("Количество уникальных номеров: ${uniqueNumbers.size}")

    val totalLength = phoneNumbers.sumOf { it.length }
    println("Сумма длин всех номеров: $totalLength")

    val namesByNumber = mutableMapOf<String, String>()
    uniqueNumbers.forEach { number ->
        print("Введите имя человека с номером телефона $number: ")
        val name = readLine() ?: ""
        namesByNumber[number] = name
    }

    namesByNumber.forEach { (number, name) ->
        println("Абонент: $name. Номер телефона: $number")
    }

    namesByNumber.toSortedMap().forEach { (number, name) ->
        println("Абонент: $name. Номер телефона: $number")
    }
    namesByNumber.toSortedMap(compareBy { (key,value)-> value })

}

fun getNumbers(): MutableList<String> {
    var N: Int
    while (true) {
        print("Введите количество номеров (n > 0): ")
        val input = readlnOrNull()
        N = input?.toIntOrNull() ?: 0

        if (N > 0) {
            break
        } else {
            println("Ошибка ввода. Пожалуйста, введите число больше 0.")
        }
    }

    val phoneNumbers = mutableListOf<String>()
    for (i in 1..N) {
        print("Введите $i-й номер телефона: ")
        val number = readLine() ?: ""
        phoneNumbers.add(number)
    }

    return phoneNumbers
}




