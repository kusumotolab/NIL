package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.NGrams

class SymbolSeparator : Tokenizer {
    companion object {
        private val symbols = charArrayOf(
            '`',
            '~',
            '!',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*',
            '(',
            ')',
            '-',
            '+',
            '=',
            '{',
            '[',
            '}',
            ']',
            '|',
            '\\',
            ':',
            ';',
            '\"',
            '\'',
            '<',
            ',',
            '>',
            '.',
            '/',
            '?',
            ' ',
            '\n',
            '\r',
            '\t'
        )
    }

    override fun tokenize(text: String): NGrams =
        text.split(*symbols)
            .filter { it.isNotEmpty() }
            .map { it.hashCode() }
}
