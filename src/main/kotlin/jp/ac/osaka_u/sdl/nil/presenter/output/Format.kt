package jp.ac.osaka_u.sdl.nil.presenter.output

import jp.ac.osaka_u.sdl.nil.NILMain
import jp.ac.osaka_u.sdl.nil.NILMain.Companion.CODE_BLOCK_FILE_NAME
import java.io.File

abstract class Format {
    fun convert(outputFileName: String) =
        File(outputFileName).bufferedWriter().use { bw ->
            val codeBlocks: List<String> = File(CODE_BLOCK_FILE_NAME).readLines()
            File(NILMain.CLONE_PAIR_FILE_NAME).bufferedReader().use { br ->
                br.lines()
                    .map { line ->
                        val (id1, id2) = line.split(",")
                        val codeBlock1 = codeBlocks[id1.toInt()]
                        val codeBlock2 = codeBlocks[id2.toInt()]
                        reformat(codeBlock1, codeBlock2)
                    }
                    .forEach { bw.appendLine(it) }
            }
        }

    protected abstract fun reformat(codeBlock1: String, codeBlock2: String): String
}
