package io.github.t45k.lvmapper

import java.io.File

class LVMapperConfig(val src: File)

fun parseArgs(args: Array<String>): LVMapperConfig =
    // TODO impl
    LVMapperConfig(File(args[0]))
