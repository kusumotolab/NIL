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
                        override fun enterMethod_declaration(ctx: CSharpParser.Method_declarationContext) {
                            val startLine = ctx.start.line
                            val endLine = ctx.stop.line
                            if (endLine - startLine + 1 < config.minLine) {
                                return
                            }

                            val startToken: Int = ctx.sourceInterval.a
                            val endToken: Int = ctx.sourceInterval.b
                            val filteredTokens = tokens.get(startToken, endToken).filterNot { it.text.isNegligible() }
                            if (filteredTokens.size >= config.minToken) {
                                val tokenSequence =
                                    SymbolSeparator.separate(filteredTokens.map { it.text })
                                emitter.onNext(CodeBlock(srcFile.canonicalPath, startLine, endLine, tokenSequence))
                            }
                        }
                    }.also { ParseTreeWalker.DEFAULT.walk(it, parser.compilation_unit()) }
                }
            emitter.onComplete()
        }.toFlowable(BackpressureStrategy.BUFFER)

    private fun String.isNegligible(): Boolean =
        this[0] == '\n' || this[0] == ' ' || this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
}
