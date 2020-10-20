package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.NGrams

interface Tokenizer {
    fun tokenize(text: String): NGrams
}
