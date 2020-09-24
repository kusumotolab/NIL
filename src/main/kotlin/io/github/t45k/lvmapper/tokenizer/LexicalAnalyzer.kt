package io.github.t45k.lvmapper.tokenizer

import io.github.t45k.lvmapper.entity.TokenSequence
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.IScanner
import org.eclipse.jdt.core.compiler.ITerminalSymbols
import org.eclipse.jdt.core.compiler.InvalidInputException

class LexicalAnalyzer : Tokenizer {
    override fun tokenize(text: String): TokenSequence =
        ToolFactory
            .createScanner(false, false, true, "14", "14", true)
            .also { it.source = text.toCharArray() }
            .let { scanner ->
                generateSequence { 0 }
                    .map { scanner.getNextTokenIfValid() }
                    .takeWhile { it != ITerminalSymbols.TokenNameEOF }
                    .map { String(scanner.currentTokenSource).hashCode() }
                    .toList()
            }

    private fun IScanner.getNextTokenIfValid() =
        try {
            this.nextToken
        } catch (e: InvalidInputException) {
            ITerminalSymbols.TokenNameEOF
        }
}
