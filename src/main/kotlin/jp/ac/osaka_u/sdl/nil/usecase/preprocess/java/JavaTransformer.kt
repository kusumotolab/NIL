package jp.ac.osaka_u.sdl.nil.usecase.preprocess.java

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.entity.CodeBlock
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.SymbolSeparator
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.AST.JLS21
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.MethodDeclaration
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor
import java.io.File

class JavaTransformer(private val config: NILConfig) {

    fun extractBlocks(sourceFile: File): Flowable<CodeBlock> =
        Observable.create<CodeBlock> { emitter ->
            val compilationUnit: CompilationUnit = ASTParser.newParser(JLS21)
                .apply { setSource(sourceFile.readText().toCharArray()) }
                .apply {
                    setCompilerOptions(
                        JavaCore.getOptions().apply { put(JavaCore.COMPILER_SOURCE, "21") })
                }
                .let { it.createAST(NullProgressMonitor()) as CompilationUnit }

            val fileName = sourceFile.canonicalPath
            object : ASTVisitor() {
                override fun visit(node: MethodDeclaration): Boolean {
                    val startLine: Int = compilationUnit.getStartLineNumber(node)
                    val endLine: Int = compilationUnit.getEndLineNumber(node)
                    node.javadoc = null
                    if (endLine - startLine + 1 < config.minLine) {
                        return false
                    }

                    val tokens: List<String> = JavaLexer.tokenize(node.toString())
                    if (tokens.size >= config.minToken) {
                        emitter.onNext(CodeBlock(fileName, startLine, endLine, SymbolSeparator.separate(tokens)))
                    }
                    return false
                }
            }.apply(compilationUnit::accept)
            emitter.onComplete()
        }.toFlowable(BackpressureStrategy.BUFFER)

    private fun CompilationUnit.getStartLineNumber(node: MethodDeclaration): Int =
        if (node.javadoc == null) {
            this.getLineNumber(node.startPosition)
        } else {
            this.getLineNumber(node.getNodeNextToJavaDoc().startPosition)
        }

    private fun CompilationUnit.getEndLineNumber(node: ASTNode): Int =
        this.getLineNumber(node.startPosition + node.length)

    @Suppress("UNCHECKED_CAST")
    private fun MethodDeclaration.getNodeNextToJavaDoc(): ASTNode =
        (structuralPropertiesForType() as List<StructuralPropertyDescriptor>)
            .asSequence()
            .drop(1)
            .flatMap {
                when (it) {
                    is ChildListPropertyDescriptor -> (getStructuralProperty(it) as List<ASTNode>).asSequence()
                    is SimplePropertyDescriptor -> emptySequence()
                    else -> sequenceOf(getStructuralProperty(it) as ASTNode?)
                }
            }
            .filterNotNull()
            .first()
}
