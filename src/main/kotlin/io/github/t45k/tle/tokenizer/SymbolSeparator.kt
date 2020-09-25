package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.TokenSequence

class SymbolSeparator : Tokenizer {
    companion object {
        private val symbols = arrayOf(
            "`",
            "~",
            "!",
            "@",
            "#",
            "$",
            "%",
            "^",
            "&",
            "*",
            "(",
            ")",
            "-",
            "+",
            "=",
            "{",
            "[",
            "}",
            "]",
            "|",
            "\\",
            ":",
            ";",
            "\"",
            "'",
            "<",
            ",",
            ">",
            ".",
            "/",
            "?",
            " ",
            "\n",
            "\r",
            "\t"
        )
    }

    override fun tokenize(text: String): TokenSequence =
        text.split(*symbols)
            .filter { it.isNotBlank() }
            .map { it.hashCode() }
}
