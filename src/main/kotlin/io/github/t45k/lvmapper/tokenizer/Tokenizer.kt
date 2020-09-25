package io.github.t45k.lvmapper.tokenizer

import io.github.t45k.lvmapper.entity.TokenSequence

interface Tokenizer {
    fun tokenize(text: String): TokenSequence
}
