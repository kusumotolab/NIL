package jp.ac.osaka_u.sdl.nil.usecase.preprocess

import jp.ac.osaka_u.sdl.nil.entity.TokenSequence

/**
 * This class converts a code blocks as a text into token sequence.
 * This process is language-independent.
 */
class SymbolSeparator {
    companion object {
        private val symbols = setOf(
            '`',
            '~',
            '!',
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
            ':',
            ';',
            '<',
            ',',
            '>',
            '.',
            '/',
            '?',
        )

        fun separate(tokens: List<String>): TokenSequence =
            tokens.filterNot { symbols.contains(it[0]) }
                .map { it.hashCode() }
    }
}
