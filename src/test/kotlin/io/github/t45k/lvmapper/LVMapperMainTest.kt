package io.github.t45k.lvmapper

import java.io.File
import kotlin.test.Test

internal class LVMapperMainTest {

    @Test
    fun test() {
        val config = LVMapperConfig(File("/Users/laevatein/prog"))
        LVMapperMain(config).run()
    }
}
