package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.TokenSequence
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

class LexicalAnalyzer : Tokenizer {

    override fun tokenize(text: String): TokenSequence =
        ToolFactory.createScanner(false, false, false, "14")
            .also { it.source = text.toCharArray() }
            .let { scanner ->
                generateSequence { 0 }
                    .map { scanner.nextToken }
                    .takeWhile { it != TokenNameEOF }
                    .toList()
            }
}
