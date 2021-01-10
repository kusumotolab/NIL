package io.github.t45k.nil.usecase

import io.github.t45k.nil.entity.TokenSequence

interface Verification {
    fun verify(tokenSequence1: TokenSequence, tokenSequence2: TokenSequence): Boolean
}
