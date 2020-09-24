package io.github.t45k.lvmapper.tokenizer

import io.github.t45k.lvmapper.entity.TokenSequence

class LexicalAnalyzer : Tokenizer {
    companion object {
        private val symbols = setOf(
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
            '"',
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

    override fun tokenize(text: String): TokenSequence {
        val builder = StringBuilder()
        val tokens = mutableListOf<String>()
        text.forEach {
            if (symbols.contains(it)) {
                if (builder.isNotBlank()) {
                    tokens.add(builder.toString())
                    builder.clear()
                }
                tokens.add(it.toString())
            } else {
                builder.append(it)
            }
        }

        return tokens.filter { it.isNotBlank() }
            .map { it.hashCode() }
    }
}
