package io.github.t45k.lvmapper

import java.nio.file.Paths
import kotlin.test.Test

internal class LVMapperMainTest {

    @Test
    fun test() {
        val config = LVMapperConfig(Paths.get("src/test/resources/examples"))
        LVMapperMain(config).run()
    }
}
