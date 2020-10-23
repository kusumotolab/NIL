package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.TokenSequence

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

    override fun tokenize(text: String): TokenSequence =
        text.split(*symbols)
            .filter { it.isNotEmpty() }
            .map { it.hashCode() }
}
