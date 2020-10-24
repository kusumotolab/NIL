package io.github.t45k.nil.tokenizer

import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

class LexicalAnalyzer {
    companion object {
        fun countTokens(text: String): Int =
            ToolFactory.createScanner(false, false, false, "14")
                .also { it.source = text.toCharArray() }
                .let { scanner ->
                    generateSequence { 0 }
                        .map { scanner.nextToken }
                        .takeWhile { it != TokenNameEOF }
                        .count()
                }
    }
}
