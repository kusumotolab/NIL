package io.github.t45k.nil.entity

// しばらくはメソッドオンリー
data class CodeBlock(
    val fileName: String,
    val startLine: Int,
    val endLine: Int,
    val tokenSequence: TokenSequence,
)
