package io.github.t45k.nil.entity

/**
 * Code block is a single function.
 */
data class CodeBlock(
    val fileName: String,
    val startLine: Int,
    val endLine: Int,
    val tokenSequence: TokenSequence,
) {
    override fun toString(): String =
        "${fileName},${startLine},${endLine}"
}
