package io.github.t45k.lvmapper

import java.io.File
import kotlin.test.Test

internal class LVMapperMainTest{

    @Test
    fun test(){
        val config = LVMapperConfig(File("src/test/resources/examples"))
        LVMapperMain(config).run()
    }
}
