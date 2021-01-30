package jp.ac.osaka_u.sdl.nil.usecase.cloneDetection

import jp.ac.osaka_u.sdl.nil.entity.TokenSequence

interface Verification {
    fun verify(tokenSequence1: TokenSequence, tokenSequence2: TokenSequence): Boolean
}
