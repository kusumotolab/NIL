package io.github.t45k.tle.output

import io.github.t45k.tle.entity.CodeBlock

interface Format {
    fun output(fileName: String, clonePairs: List<Pair<Int, Int>>, codeBlocks: List<CodeBlock>)
}
