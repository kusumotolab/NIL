package io.github.t45k.nil.output

class CSV : FormatConverter() {
    override fun reformat(codeBlock1: String, codeBlock2: String): String =
        "$codeBlock1,$codeBlock2"
}
