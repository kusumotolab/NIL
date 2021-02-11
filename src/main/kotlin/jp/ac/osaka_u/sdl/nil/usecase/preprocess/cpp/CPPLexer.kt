package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp

import antlr.cpp.CPP14Lexer
import jp.ac.osaka_u.sdl.nil.util.toCharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token

class CPPLexer {
    companion object {
        private const val BLOCK_COMMENT = 149
        private const val LINE_COMMENT = 150

        fun tokenize(text: String): List<String> =
            text.toCharStream()
                .let(::CPP14Lexer)
                .let(::CommonTokenStream)
                .apply { fill() }
                .tokens
                .filterNot { it.isComment() }
                .map { it.text }
                .filterNot { it.isNotBlank() && it != "<EOF>" }

        private fun Token.isComment(): Boolean =
            this.type == BLOCK_COMMENT || this.type == LINE_COMMENT
    }
}
