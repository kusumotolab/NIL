package io.github.t45k.lvmapper.entity

import java.io.File

// しばらくはメソッドオンリー
data class CodeBlock(val file: File, val startLine: Int, val endLine: Int, val text: String) {
    lateinit var prettyPrint: List<Int>
}
