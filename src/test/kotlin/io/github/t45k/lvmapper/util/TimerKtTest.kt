package io.github.t45k.lvmapper.util

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimerKtTest {
    @Test
    fun testTimer() {
        assertEquals("1m", 60.toTime())
        assertEquals("1h", 3600.toTime())
        assertEquals("1h1m1s", 3661.toTime())
    }
}
