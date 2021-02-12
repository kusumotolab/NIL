package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cs

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.entity.CodeBlock
import jp.ac.osaka_u.sdl.nil.parser.cs.CSharpLexer
import jp.ac.osaka_u.sdl.nil.parser.cs.CSharpParser
import jp.ac.osaka_u.sdl.nil.parser.cs.CSharpParserBaseListener
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.SymbolSeparator
import jp.ac.osaka_u.sdl.nil.util.toCharStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class CSharpTransformer(private val config: NILConfig) {
    fun extractBlocks(srcFile: File): Flowable<CodeBlock> =
        Observable.create<CodeBlock> { emitter ->
            val tokens: CommonTokenStream = srcFile.readText().toCharStream()
                .let(::CSharpLexer)
                .let(::CommonTokenStream)
                .apply { fill() }
            tokens.let(::CSharpParser)
                .also { parser ->
                    object : CSharpParserBaseListener() {
                        override fun enterLocal_function_declaration(ctx: CSharpParser.Local_function_declarationContext) {
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
                    }.also { ParseTreeWalker.DEFAULT.walk(it, parser.compilation_unit()) }
                }
            emitter.onComplete()
        }.toFlowable(BackpressureStrategy.BUFFER)
}
