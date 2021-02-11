package jp.ac.osaka_u.sdl.nil.util

import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimerKtTest {
    @Test
    fun testTimer() {
        assertEquals("1s", 1000.toTime())
        assertEquals("1m", 60000.toTime())
        assertEquals("1h", 3600000.toTime())
        assertEquals("1h1m1s", 3661000.toTime())
    }
}
