package io.github.t45k.nil

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NILMainTest {

    @Test
    fun test() {
        val config = parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bcb"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = "examples,LargeGapClones.java,3,14,examples,LargeGapClones.java,16,39" +
            System.lineSeparator() +
            "examples,LargeVarianceClones.java,3,21,examples,LargeVarianceClones.java,23,54\n"

        assertEquals(result, outputFile.readText())
        outputFile.delete()
    }
}
