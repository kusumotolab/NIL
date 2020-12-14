package io.github.t45k.nil.output

import io.github.t45k.nil.entity.CodeBlock
import java.io.File

class CSV : Format {
    override fun output(fileName: String, clonePairs: List<Pair<Int, Int>>, codeBlocks: List<CodeBlock>) {
        val result = clonePairs.joinToString(System.lineSeparator()) {
            "${reformat(codeBlocks[it.first])},${reformat(codeBlocks[it.second])}"
        }
        File(fileName).writeText(result)
    }

    private fun reformat(codeBlock: CodeBlock): String =
        "${codeBlock.fileName},${codeBlock.startLine},${codeBlock.endLine}"
}
