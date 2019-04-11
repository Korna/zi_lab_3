var message: String = "Храните деньги в сберегательной кассе."
var key: String = "Артемий"
const val BLOCK_STEP = 2
const val MAX_BITS_VALUE = 255
const val BLOCK_SIZE = 16
const val DEBUG = true
fun main(args: Array<String>) {
    val iterations = message.length
    val ascii_message = message.toAsciiList()
    ascii_message.forEach { println("Ascii msg:$it") }
    val ascii_key = key.toAsciiList()
    ascii_key.forEach { println("Ascii key:$it") }

    val encryptedMessage = encryptMessage(iterations, ascii_message, ascii_key)
    println("Encrypted symbol codes:${encryptedMessage}")
    println("Encrypted message:${encryptedMessage.toNormalString()}")
    val decryptedMessage = decryptMessage(iterations, encryptedMessage, ascii_key)
    println("Decrypted symbol ASCII codes:${decryptedMessage}")
    println("Decrypted message:${decryptedMessage.toNormalString()}")
}

/**
 * Дешифрование при помощи ключа
 * @return зашифрованный текст в виде списка ascii чисел
 */
fun encryptMessage(
    iterations: Int,
    ascii_message: List<Int>,
    ascii_key: List<Int>
): List<Int> {
    val listOfAscii = mutableListOf<Int>()
    for (index in 0 until iterations step BLOCK_STEP) {
        printMessage("Блок для шифрования:$index")
        var Lp = ascii_message[index]
        var Rp = ascii_message[index + 1]
        for (lap in 0 until BLOCK_SIZE step 1) {
            val lapKey = ascii_key.accessKeySymbol(lap)
            if (index == 0)
                printMessage("Ключ раунда:$lapKey Rp:$Rp Lp $Lp")
            val function = operationFunction(Lp, lapKey)
            val Op = (Rp xor function)
            if (lap != (BLOCK_SIZE - 1)) {
                Rp = Lp
                Lp = Op
            } else {
                Rp = Op
            }
        }
        listOfAscii.add(Lp)
        listOfAscii.add(Rp)
    }
    return listOfAscii
}

/**
 * Дешифрование при помощи ключа
 */
fun decryptMessage(
    iterations: Int,
    ascii_message: List<Int>,
    ascii_key: List<Int>
): List<Int> {
    val listOfAscii = mutableListOf<Int>()
    for (index in 0 until iterations step BLOCK_STEP) {
        printMessage("Блок для дешифровки:$index")
        var Lp = ascii_message[index]
        var Rp = ascii_message[index + 1]
        for (lap in (BLOCK_SIZE - 1) downTo 0) {
            val lapKey = ascii_key.accessKeySymbol(lap)
            if (index == 0)
                printMessage("Ключ раунда:$lapKey Rp:$Rp Lp $Lp")
            val function = operationFunction(Lp, lapKey)
            val Op = (Rp xor function)
            if (lap != 0) {
                Rp = Lp
                Lp = Op
            } else {
                Rp = Op
            }
        }
        listOfAscii.add(Lp)
        listOfAscii.add(Rp)
    }
    return listOfAscii
}

/**
 * Взять символ из ключа с кратностью, равной его длине
 */
fun List<Int>.accessKeySymbol(index: Int): Int = this[index % this.size]

/**
 * Операционная функция
 */
fun operationFunction(lp: Int, key: Int) = lp * Math.abs((Math.pow(2.0, key.toDouble()) + 1).toInt())

/**
 * Перевод текста в список ascii значений
 */
fun String.toAsciiList(): List<Int> = this.map(Char::toInt)

/**
 * Перевод списка ascii значений в текст
 */
fun List<Int>.toNormalString(): String = this.map { it.toChar() }.joinToString("")

fun printMessage(text: String): Unit = if (DEBUG) println(text) else Unit