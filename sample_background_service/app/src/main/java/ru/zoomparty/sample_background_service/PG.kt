package ru.zoomparty.sample_background_service

fun main() {
    val phoneNumbers = getNumbers()

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




