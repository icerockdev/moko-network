import kotlin.native.internal.GC

fun collect() {
    GC.collect()
}

fun cycles() {
    val result = GC.detectCycles()
    println("cycles: ${result?.size}")
    result?.forEach {
        println("cycle: $it")
    }
}
