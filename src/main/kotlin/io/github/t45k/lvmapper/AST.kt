package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import java.io.File

class AST(private val file: File) {
    private val compilationUnit: CompilationUnit = ASTParser.newParser(AST.JLS14)
        .also { it.setSource(file.readText().toCharArray()) }
        .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

    fun extractBlocks(): List<CodeBlock> {
        val methods: MutableList<CodeBlock> = mutableListOf()
        val visitor = object : ASTVisitor() {
            override fun visit(node: MethodDeclaration?): Boolean {
                val body: Block = node?.body ?: return false
                methods.add(
                    CodeBlock(
                        file,
                        compilationUnit.getLineNumber(body.startPosition),
                        compilationUnit.getLineNumber(body.startPosition + body.length),
                        body.toString(),
                    )
                )
                return false
            }
        }
        compilationUnit.accept(visitor)
        return methods
    }
}
