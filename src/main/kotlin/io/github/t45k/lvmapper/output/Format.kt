package io.github.t45k.lvmapper.output

import io.github.t45k.lvmapper.entity.CodeBlock

interface Format {
    fun output(fileName: String, clonePairs: List<Pair<Int, Int>>, codeBlocks: List<CodeBlock>)
}
