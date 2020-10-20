package io.github.t45k.nil.output

import io.github.t45k.nil.entity.CodeBlock

interface Format {
    fun output(fileName: String, clonePairs: List<Pair<Int, Int>>, codeBlocks: List<CodeBlock>)
}
