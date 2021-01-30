package jp.ac.osaka_u.sdl.nil.presenter.output

import java.io.File

/**
 * BigCloneEval Format is as "dir1,file_name1,start_line1,end_line1,dir2,file_name2,start_line2,end_line2"
 */
class BCEFormat : Format() {
    override fun reformat(codeBlock1: String, codeBlock2: String): String =
        "${removeUnusedInformation(codeBlock1)},${removeUnusedInformation(codeBlock2)}"

    private fun removeUnusedInformation(codeBlock: String): String {
        val (filePath, startLine, endLine) = codeBlock.split(",")
        val (dirName, fileName) = filePath.split(File.separator).let { it[it.size - 2] to it.last() }
        return "$dirName,$fileName,$startLine,$endLine"
    }
}
