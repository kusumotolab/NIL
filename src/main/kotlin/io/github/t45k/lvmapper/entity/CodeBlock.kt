package io.github.t45k.lvmapper.entity

import java.io.File

// しばらくはメソッドオンリー
data class CodeBlock(
    val path: String,
    val startLine: Int,
    val endLine: Int,
    val tokenSequence: TokenSequence,
)
