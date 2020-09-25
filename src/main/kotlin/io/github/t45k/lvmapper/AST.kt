package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST.JLS14
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import java.io.File

class AST(private val tokenizer: (String) -> List<Int>) {
    companion object {
        private const val LINE_THRESHOLD = 6
    }

    fun extractBlocks(sourceFile: File): List<CodeBlock> {
        val compilationUnit: CompilationUnit = ASTParser.newParser(JLS14)
            .also { it.setSource(sourceFile.readText().toCharArray()) }
            .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

        val codeBlocks: MutableList<CodeBlock> = mutableListOf()
        val visitor = object : ASTVisitor() {
            override fun visit(node: MethodDeclaration?): Boolean {
                node?.body?.let { body: Block ->
                    val fileName = sourceFile.toString()
                    val startLine = compilationUnit.getLineNumber(body.startPosition)
                    val endLine = compilationUnit.getLineNumber(body.startPosition + body.length)
                    if (endLine - startLine + 1 >= LINE_THRESHOLD) {
                        val tokenSequence = tokenizer(body.toString())
                        codeBlocks.add(CodeBlock(fileName, startLine, endLine, tokenSequence))
                    }
                }
                return false
            }
        }
        compilationUnit.accept(visitor)
        return codeBlocks
    }
}
