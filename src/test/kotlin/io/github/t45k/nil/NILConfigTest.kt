package io.github.t45k.nil

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class NILConfigTest {

    @Test
    fun testBceAndMifSpecified() {
        val exception = assertFailsWith<InvalidOptionException> { parseArgs(arrayOf("-bce", "-mif")) }
        assertTrue { exception.message.contains("Cannot specify both -bce and -mif") }
    }
}
