package io.github.t45k.lvmapper.util

fun Long.toTime(): String {
    val builder = StringBuilder()
    if (this >= 3600) {
        builder.append("${this / 3600}h")
    }
    if (this >= 60) {
        val minutes = this % 3600 / 60
        if (minutes > 0) {
            builder.append("${minutes}m")
        }
    }
    val seconds = this % 60
    if (seconds > 0) {
        builder.append("${seconds}s")
    }
    return builder.toString()
}

fun Int.toTime(): String = this.toLong().toTime()
