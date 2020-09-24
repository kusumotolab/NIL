package io.github.t45k.lvmapper

import Java8Lexer
import Java8Parser
import Java8ParserBaseListener
import io.github.t45k.lvmapper.entity.CodeBlock
import io.github.t45k.lvmapper.entity.TokenSequence
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File

class AST(private val tokenizer: (String) -> List<Int>) {

    fun extractBlocks(file: File): List<CodeBlock> {
        val codeBlocks: MutableList<CodeBlock> = mutableListOf()
        val visitor = object : Java8ParserBaseListener() {
            override fun enterMethodBody(ctx: Java8Parser.MethodBodyContext?) {
                val startLine: Int = ctx?.start?.line ?: return
                val endLine: Int = ctx.stop?.line ?: return
                if (endLine - startLine - 1 >= 6) {
                    val tokenSequence: TokenSequence = tokenizer(ctx.text)
                    val codeBlock = CodeBlock(file, startLine, endLine, tokenSequence)
                    codeBlocks.add(codeBlock)
                }
            }
        }
        ParseTreeWalker().walk(visitor, parseFile(file))
        return codeBlocks
    }

    private fun parseFile(file: File): Java8Parser.CompilationUnitContext =
        file.readText()
            .let { CharStreams.fromString(it) }
            .let { Java8Lexer(it) }
            .let { CommonTokenStream(it) }
            .also { it.fill() }
            .let { Java8Parser(it) }
            .compilationUnit()
}
