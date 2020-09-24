package io.github.t45k.lvmapper

fun convert(time: Long): String {
    val builder = StringBuilder()
    if (time >= 3600) {
        builder.append("${time / 3600}h")
    }
    if (time >= 60) {
        val minutes = time / 3600 / 60
        if (minutes > 0) {
            builder.append("${minutes}m")
        }
    }
    return builder.append("${time / 60}s").toString()
}
