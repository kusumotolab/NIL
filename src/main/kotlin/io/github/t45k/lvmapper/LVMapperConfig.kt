package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.TokenizeMethod.LEXICAL_ANALYSIS
import java.io.File

data class LVMapperConfig(
    val src: File,
    val windowSize: Int = 3,
    val tokenizeMethod: TokenizeMethod = LEXICAL_ANALYSIS
)

enum class TokenizeMethod {
    LEXICAL_ANALYSIS,
    SYMBOL_SEPARATION,
}

fun parseArgs(args: Array<String>): LVMapperConfig =
    // TODO impl
    LVMapperConfig(File(args[0]))
