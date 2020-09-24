package io.github.t45k.lvmapper

import java.nio.file.Paths
import kotlin.test.Test

internal class LVMapperMainTest {

    @Test
    fun test() {
        val config = LVMapperConfig(Paths.get("/Users/laevatein/prog/jruby/core/src/main/java"))
        LVMapperMain(config).run()
    }
}
