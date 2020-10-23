package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.TokenSequence

interface Tokenizer {
    fun tokenize(text: String): TokenSequence
}
