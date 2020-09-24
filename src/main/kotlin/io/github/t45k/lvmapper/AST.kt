package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import java.io.File

class AST(private val file: File, private val tokenizer: (String) -> List<Int>) {
    private val compilationUnit: CompilationUnit = ASTParser.newParser(AST.JLS14)
        .also { it.setSource(file.readText().toCharArray()) }
        .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

    fun extractBlocks(): List<CodeBlock> {
        val methods: MutableList<CodeBlock> = mutableListOf()
        val visitor = object : ASTVisitor() {
            override fun visit(node: MethodDeclaration?): Boolean {
                node?.body?.let { body ->
                    val startLine: Int = compilationUnit.getLineNumber(body.startPosition)
                    val endLine: Int = compilationUnit.getLineNumber(body.startPosition + body.length)
                    if (endLine - startLine >= 6) {
                        val tokenSequence = tokenizer(body.toString())
                        val codeBlock = CodeBlock(file, startLine, endLine, tokenSequence)
                        methods.add(codeBlock)
                    }
                }
                return false
            }
        }
        compilationUnit.accept(visitor)
        return methods
    }
}
