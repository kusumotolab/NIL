package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.TokenizeMethod.SYMBOL_SEPARATION
import java.nio.file.Path
import java.nio.file.Paths

data class LVMapperConfig(
    val src: Path,
    val windowSize: Int = 5,
    val tokenizeMethod: TokenizeMethod = SYMBOL_SEPARATION
)

enum class TokenizeMethod {
    LEXICAL_ANALYSIS,
    SYMBOL_SEPARATION,
}

fun parseArgs(args: Array<String>): LVMapperConfig =
    // TODO impl
    LVMapperConfig(Paths.get(args[0]))
