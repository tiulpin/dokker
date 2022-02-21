import com.sun.tools.javac.tree.TreeInfo.args
import java.io.BufferedReader
import java.io.File
import java.math.BigDecimal
import java.nio.CharBuffer
import java.nio.file.Files
import java.util.*
import kotlin.collections.HashMap

fun check(a: Boolean?) {
    if (a ?: false) throw IllegalStateException()
}


fun main(args : Array<String>) {
    val configLocation = File(args[1])

    var configReader: BufferedReader? = null
    val configBuf = CharBuffer.wrap(String())
    val hm = HashMap<String, BigDecimal>()

    val immutableList = listOf(1, 2)
    Collections.reverse(immutableList)

    val e5 = "sffsdf"
    val a = 3
    val b = BigDecimal(44.32)
    hm["f"] = BigDecimal(3.1)

    synchronized(a) {}

    try {
        configReader = Files.newBufferedReader(configLocation.toPath())
        configReader.read(configBuf)
    } catch (ignored: Throwable) {
        ignored.printStackTrace()
    }
    println("Hello, World!")
}