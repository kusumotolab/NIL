package jp.ac.osaka_u.sdl.nil

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

internal class NILMainTest {

    @Test
    fun test() {
        val config = parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = listOf(
            "examples,AssertContainClones.java,2,27,examples,AssertContainClones.java,30,55",
            "examples,LargeGapClones.java,3,14,examples,LargeGapClones.java,16,39",
            "examples,LargeVarianceClones.java,3,21,examples,LargeVarianceClones.java,23,54",
            ""
        )

        assertEquals(result.sorted(), outputFile.readText().lines().sorted())
        outputFile.delete()
    }

    @Test
    fun testCPP() {
        val config =
            parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1", "-l", "cpp"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = listOf(
            "examples,FizzBuzz.cpp,4,39,examples,FizzBuzz.cpp,41,78",
            ""
        )

        assertEquals(result.sorted(), outputFile.readText().lines().sorted())
        outputFile.delete()
    }

    @Test
    fun testCSharp() {
        val config =
            parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1", "-l", "cs"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = listOf(
            "examples,FizzBuzz.cs,3,33,examples,FizzBuzz.cs,35,67",
            ""
        )

        assertEquals(result.sorted(), outputFile.readText().lines().sorted())
        outputFile.delete()
    }

    @Test
    fun testPython() {
        val config =
            parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1", "-l", "py"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = listOf(
            "examples,FizzBuzz.py,1,13,examples,FizzBuzz.py,13,24",
            ""
        )

        assertEquals(result.sorted(), outputFile.readText().lines().sorted())
        outputFile.delete()
    }

    @Test
    fun testKotlin() {
        val config =
            parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1", "-l", "kt"))
        NILMain(config).run()

        val outputFile = File(config.outputFileName)
        val result = listOf(
            "examples,FizzBuzz.kt,1,13,examples,FizzBuzz.kt,15,29",
            ""
        )

        assertEquals(result.sorted(), outputFile.readText().lines().sorted())
        outputFile.delete()
    }
}
