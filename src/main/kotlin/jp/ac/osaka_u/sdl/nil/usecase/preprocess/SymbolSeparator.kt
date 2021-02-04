package jp.ac.osaka_u.sdl.nil.usecase.preprocess

import jp.ac.osaka_u.sdl.nil.entity.TokenSequence

/**
 * This class converts a code blocks as a text into token sequence.
 * This process is language-independent.
 */
class SymbolSeparator {
    companion object {
        val symbols = charArrayOf(
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
