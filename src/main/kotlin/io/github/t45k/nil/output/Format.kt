package io.github.t45k.nil.output

import java.io.File

abstract class Format {
    fun convert(outputFileName: String, codeBlockFile: File, clonePairFile: File) =
        File(outputFileName).bufferedWriter().use { bw ->
            val codeBlocks: List<String> = codeBlockFile.readLines()
            clonePairFile.bufferedReader().use { br ->
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
