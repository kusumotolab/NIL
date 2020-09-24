package io.github.t45k.lvmapper.tokenizer

import Java8Lexer
import io.github.t45k.lvmapper.entity.TokenSequence
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class LexicalAnalyzer : Tokenizer {
    override fun tokenize(text: String): TokenSequence =
        CharStreams.fromString(text)
            .let(::Java8Lexer)
            .let(::CommonTokenStream)
            .also(CommonTokenStream::fill)
            .tokens
            .map { it.text.hashCode() }
}
