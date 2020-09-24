package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST.JLS14
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import java.nio.file.Files
import java.nio.file.Path

class AST(private val tokenizer: (String) -> List<Int>) {

    fun extractBlocks(path: Path): List<CodeBlock> {
        val compilationUnit: CompilationUnit = ASTParser.newParser(JLS14)
            .also { it.setSource(Files.readString(path).toCharArray()) }
            .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

        val methods: MutableList<CodeBlock> = mutableListOf()
        val visitor = object : ASTVisitor() {
            override fun visit(node: MethodDeclaration?): Boolean {
                node?.body?.let { body: Block ->
                    val fileName = path.toString()
                    val startLine = compilationUnit.getLineNumber(body.startPosition)
                    val endLine = compilationUnit.getLineNumber(body.startPosition + body.length)
                    val tokenSequence = tokenizer(body.toString())
                    methods.add(CodeBlock(fileName, startLine, endLine, tokenSequence))
                }
                return false
            }
        }
        compilationUnit.accept(visitor)
        return methods
    }
}
