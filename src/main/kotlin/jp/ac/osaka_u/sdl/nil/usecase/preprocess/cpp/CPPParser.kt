package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp

import antlr.cpp.CPP14Lexer
import antlr.cpp.CPP14Parser
import antlr.cpp.CPP14ParserBaseListener
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.entity.CodeBlock
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.SymbolSeparator
import jp.ac.osaka_u.sdl.nil.util.toCharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class CPPParser(private val config: NILConfig) {
    fun extractBlocks(srcFile: File): Flowable<CodeBlock> =
        Observable.create<CodeBlock> { emitter ->
            srcFile.readText().toCharStream()
                .let(::CPP14Lexer)
                .let(::CommonTokenStream)
                .apply { fill() }
                .let(::CPP14Parser)
                .run {
                    val walker = ParseTreeWalker()
                    object : CPP14ParserBaseListener() {
                        override fun enterFunctionDefinition(ctx: CPP14Parser.FunctionDefinitionContext) {
                            val startLine = ctx.start.line
                            val endLine = ctx.stop.line
                            if (endLine - startLine + 1 < config.minLine) {
                                return
                            }

                            val tokens: List<String> = CPPLexer.tokenize(ctx.text)
                            if (tokens.size >= config.minToken) {
                                val tokenSequence = SymbolSeparator.separate(tokens)
                                emitter.onNext(CodeBlock(srcFile.canonicalPath, startLine, endLine, tokenSequence))
                            }
                        }
                    }.also { walker.walk(it, this.translationUnit()) }
                }
        }.toFlowable(BackpressureStrategy.BUFFER)
}
