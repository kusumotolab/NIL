package io.github.t45k.tle.entity

// しばらくはメソッドオンリー
data class CodeBlock(
    val fileName: String,
    val startLine: Int,
    val endLine: Int,
    var tokenSequence: TokenSequence,
    var seedsSize: Int = -1
)
