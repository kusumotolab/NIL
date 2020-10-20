package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.NGrams

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
