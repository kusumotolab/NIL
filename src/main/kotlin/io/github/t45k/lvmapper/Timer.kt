package io.github.t45k.lvmapper

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
    return builder.append("${this % 60}s").toString()
}
