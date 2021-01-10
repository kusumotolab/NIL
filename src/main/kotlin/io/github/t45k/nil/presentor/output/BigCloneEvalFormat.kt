package io.github.t45k.nil.presentor.output

import java.io.File

class BigCloneEvalFormat : Format() {
    override fun reformat(codeBlock1: String, codeBlock2: String): String =
        "${removeUnusedInformation(codeBlock1)},${removeUnusedInformation(codeBlock2)}"

    private fun removeUnusedInformation(codeBlock: String): String {
        val (filePath, startLine, endLine) = codeBlock.split(",")
        val (dirName, fileName) = filePath.split(File.separator).let { it[it.size - 2] to it.last() }
        return "$dirName,$fileName,$startLine,$endLine"
    }
}
