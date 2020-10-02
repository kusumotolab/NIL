package io.github.t45k.tle

import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TLEMainTest {

    @Test
    fun test() {
        val config = parseArgs(arrayOf("-s", "./src/test/resources/examples", "-f", "70"))
        TLEMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = "examples,LargeGapClones.java,16,39,examples,LargeGapClones.java,3,14" +
            System.lineSeparator() +
            "examples,LargeVarianceClones.java,23,54,examples,LargeVarianceClones.java,3,21"

        assertEquals(result, outputFile.readText())
        outputFile.delete()
    }
}
