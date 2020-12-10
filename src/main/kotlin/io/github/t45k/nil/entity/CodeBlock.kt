package io.github.t45k.nil.entity

// For a while, code block is only function.
data class CodeBlock(
    val fileName: String,
    val startLine: Int,
    val endLine: Int,
    val tokenSequence: TokenSequence,
)
