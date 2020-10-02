package io.github.t45k.tle.tokenizer

import io.github.t45k.tle.entity.NGrams
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

@Deprecated("Just in case")
class LexicalAnalyzer : Tokenizer {

    override fun tokenize(text: String): NGrams =
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
