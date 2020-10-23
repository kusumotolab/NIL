package io.github.t45k.nil.tokenizer

import io.github.t45k.nil.entity.TokenSequence
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

@Deprecated("Just in case")
class LexicalAnalyzer : Tokenizer {

    override fun tokenize(text: String): TokenSequence =
        ToolFactory.createScanner(false, false, false, "14")
            .also { it.source = text.toCharArray() }
            .let { scanner ->
                generateSequence { 0 }
                    .map { scanner.nextToken }
                    .takeWhile { it != TokenNameEOF }
                    .map { scanner.currentTokenSource.hashCode() }
                    .toList()
            }
}
