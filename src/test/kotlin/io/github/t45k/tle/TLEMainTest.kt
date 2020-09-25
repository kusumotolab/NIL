package io.github.t45k.tle

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TLEMainTest {

    @Test
    fun test() {
        val config = parseArgs(arrayOf("-s", "./src/test/resources/examples"))
        LVMapperMain(config).run()

        val outputFile = File("result.csv")
        val result = "examples,LargeGapClones.java,16,39,examples,LargeGapClones.java,3,14" +
            System.lineSeparator() +
            "examples,LargeVarianceClones.java,23,54,examples,LargeVarianceClones.java,3,21"

        assertEquals(result, outputFile.readText())
        outputFile.delete()
    }
}
