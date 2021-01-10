package io.github.t45k.nil.usecase.preprocess

import io.github.t45k.nil.NILMain.Companion.CODE_BLOCK_FILE_NAME
import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.util.parallelIfSpecified
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

/**
 * This is an abstract class for Preprocessing phase.
 * If you want to extend NIL to multiple languages,
 * all you have to do is extend this class
 * and write methods to collect the source files and code blocks of the language.
 */
abstract class Preprocess(private val threads: Int) {
    companion object {
    }

    fun collectTokenSequences(src: File): List<TokenSequence> =
        File(CODE_BLOCK_FILE_NAME).bufferedWriter().use { bw ->
            collectSourceFiles(src)
                .parallelIfSpecified(threads)
                .runOn(Schedulers.io())
                .flatMap { collectBlocks(it) }
                .sequential()
                .doOnEach { it.value?.let { codeBlock -> bw.appendLine(codeBlock.toString()) } }
                .map { it.tokenSequence }
                .toList()
                .blockingGet()
        }

    protected abstract fun collectSourceFiles(dir: File): Flowable<File>
    protected abstract fun collectBlocks(srcFile: File): Flowable<CodeBlock>
}
