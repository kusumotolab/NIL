package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.entity.CodeBlock
import jp.ac.osaka_u.sdl.nil.parser.cpp.CPP14Lexer
import jp.ac.osaka_u.sdl.nil.parser.cpp.CPP14Parser
import jp.ac.osaka_u.sdl.nil.parser.cpp.CPP14ParserBaseListener
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.SymbolSeparator
import jp.ac.osaka_u.sdl.nil.util.toCharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class CPPParser(private val config: NILConfig) {
    fun extractBlocks(srcFile: File): Flowable<CodeBlock> =
        Observable.create<CodeBlock> { emitter ->
            val tokens: CommonTokenStream = srcFile.readText().toCharStream()
                .let(::CPP14Lexer)
                .let(::CommonTokenStream)
                .apply { fill() }
            tokens.let(::CPP14Parser)
                .also { parser ->
                    object : CPP14ParserBaseListener() {
                        override fun enterFunctionDefinition(ctx: CPP14Parser.FunctionDefinitionContext) {
                            val startLine = ctx.start.line
                            val endLine = ctx.stop.line
                            if (endLine - startLine + 1 < config.minLine) {
                                return
                            }

                            val startToken: Int = ctx.sourceInterval.a
                            val endToken: Int = ctx.sourceInterval.b
                            if (endToken - startToken + 1 >= config.minToken) {
                                val tokenSequence =
                                    SymbolSeparator.separate(tokens.get(startToken, endToken).map { it.text })
                                emitter.onNext(CodeBlock(srcFile.canonicalPath, startLine, endLine, tokenSequence))
                            }
                        }
                    }.also { ParseTreeWalker.DEFAULT.walk(it, parser.translationUnit()) }
                }
            emitter.onComplete()
        }.toFlowable(BackpressureStrategy.BUFFER)
}
