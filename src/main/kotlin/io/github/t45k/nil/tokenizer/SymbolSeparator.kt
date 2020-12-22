package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.TokenSequence

/**
 * This class converts a code blocks as a text into token sequence.
 * This process is language-independent.
 */
class SymbolSeparator {
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

        fun tokenize(text: String): TokenSequence =
            text.split(*symbols)
                .filter { it.isNotEmpty() }
                .map { it.hashCode() }
    }
}
