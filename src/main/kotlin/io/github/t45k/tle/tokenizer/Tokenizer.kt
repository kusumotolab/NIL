package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.NGrams

interface Tokenizer {
    fun tokenize(text: String): NGrams
}
