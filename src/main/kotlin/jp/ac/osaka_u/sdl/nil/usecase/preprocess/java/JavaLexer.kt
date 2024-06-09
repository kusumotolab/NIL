package jp.ac.osaka_u.sdl.nil.usecase.preprocess.java

import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF

class JavaLexer {
    companion object {
        fun tokenize(text: String): List<String> =
            ToolFactory.createScanner(false, false, false, "21")
                .also { it.source = text.toCharArray() }
                .let { scanner ->
                    generateSequence { 0 }
                        .map { scanner.nextToken }
                        .takeWhile { it != TokenNameEOF }
                        .map { String(scanner.currentTokenSource) }
                        .toList()
                }
    }
}
