package jp.ac.osaka_u.sdl.nil.util

/**
 * Transform milli seconds into XXhXXmXXs format.
 */
fun Long.toTime(): String {
    val builder = StringBuilder()
    val entireSeconds = this / 1000
    if (entireSeconds >= 3600) {
        builder.append("${entireSeconds / 3600}h")
    }
    if (entireSeconds >= 60) {
        val minutes = entireSeconds % 3600 / 60
        if (minutes > 0) {
            builder.append("${minutes}m")
        }
    }
    val seconds = entireSeconds % 60
    if (seconds > 0 || builder.isEmpty()) {
        builder.append("${seconds}s")
    }
    return builder.toString()
}

fun Int.toTime(): String = this.toLong().toTime()
