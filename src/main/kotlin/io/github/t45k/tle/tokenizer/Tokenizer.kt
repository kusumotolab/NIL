package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.TokenSequence

interface Tokenizer {
    fun tokenize(text: String): TokenSequence
}
