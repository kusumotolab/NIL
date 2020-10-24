package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.reactivex.rxjava3.core.Observable
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST.JLS14
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import java.io.File

class AST(private val tokenizer: (String) -> List<Int>, private val config: NILConfig) {

    fun extractBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.create { emitter ->
            val compilationUnit: CompilationUnit = ASTParser.newParser(JLS14)
                .also { it.setSource(sourceFile.readText().toCharArray()) }
                .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

            val fileName = sourceFile.toString()
            val visitor = object : ASTVisitor() {
                override fun visit(node: MethodDeclaration?): Boolean {
                    node?.let {
                        val startLine = compilationUnit.getLineNumber(it.name.startPosition)
                        val endLine = compilationUnit.getLineNumber(it.startPosition + it.length)
                        it.javadoc = null
                        if (endLine - startLine + 1 >= config.minLine) {
                            emitter.onNext(CodeBlock(fileName, startLine, endLine, tokenizer(it.toString())))
                        }
                    }
                    return false
                }
            }
            compilationUnit.accept(visitor)
            emitter.onComplete()
        }

    /*// TODO: Use rolling hash
    private fun List<Int>.toNgrams(): TokenSequence =
        (0..(this.size - config.gramSize))
            .map { this.subList(it, it + config.gramSize).hashCode() }
            .distinct()*/
}
