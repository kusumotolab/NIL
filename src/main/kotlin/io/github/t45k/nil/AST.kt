package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.tokenizer.LexicalAnalyzer
import io.reactivex.rxjava3.core.Observable
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.dom.AST.JLS14
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor
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
                        val startLine = if (it.javadoc == null) {
                            compilationUnit.getLineNumber(it.startPosition)
                        } else {
                            compilationUnit.getLineNumber(getNextNodeFromJavaDoc(node).startPosition)
                        }
                        val endLine = compilationUnit.getLineNumber(it.startPosition + it.length)
                        it.javadoc = null
                        if (endLine - startLine + 1 >= config.minLine && LexicalAnalyzer.countTokens(it.toString()) >= config.minToken) {
                            emitter.onNext(CodeBlock(fileName, startLine, endLine, tokenizer(it.toString())))
                        }
                    }
                    return false
                }

                @Suppress("UNCHECKED_CAST")
                fun getNextNodeFromJavaDoc(node: MethodDeclaration): ASTNode =
                    (node.structuralPropertiesForType() as List<StructuralPropertyDescriptor>)
                        .asSequence()
                        .drop(1)
                        .flatMap {
                            when (it) {
                                is ChildListPropertyDescriptor -> (node.getStructuralProperty(it) as List<ASTNode>).asSequence()
                                is SimplePropertyDescriptor -> emptySequence()
                                else -> sequenceOf(node.getStructuralProperty(it) as ASTNode)
                            }
                        }
                        .first()
            }
            compilationUnit.accept(visitor)
            emitter.onComplete()
        }
}
