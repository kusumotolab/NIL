package io.github.t45k.lvmapper

import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameEOF
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameLBRACE
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameRBRACE
import org.eclipse.jdt.core.compiler.ITerminalSymbols.TokenNameSEMICOLON

fun format(text: String): List<Int> =
    ToolFactory
        .createScanner(false, false, true, "14", "14", true)
        .also { it.source = text.toCharArray() }
        .let { scanner ->
            generateSequence { 0 }
                .map {
                    try {
                        scanner.nextToken
                    } catch (e: Exception) {
                        TokenNameEOF
                    }
                }
                .takeWhile { it != TokenNameEOF }
                .fold(mutableListOf<Int>() to mutableListOf<Int>(), { acc, symbol ->
                    if (symbol.isSeparator()) {
                        acc.first.also {
                            if (acc.second.isNotEmpty()) it.add(acc.second.hashCode())
                        } to mutableListOf()
                    } else {
                        acc.first to acc.second.also { it.add(symbol) }
                    }
                })
                .first
        }

private fun Int.isSeparator(): Boolean =
    when (this) {
        TokenNameRBRACE, TokenNameLBRACE, TokenNameSEMICOLON -> true
        else -> false
    }
