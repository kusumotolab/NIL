package io.github.t45k.tle.util

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimerKtTest {
    @Test
    fun testTimer() {
        assertEquals("1s", 1.toTime())
        assertEquals("1m", 60.toTime())
        assertEquals("1h", 3600.toTime())
        assertEquals("1h1m1s", 3661.toTime())
    }
}
