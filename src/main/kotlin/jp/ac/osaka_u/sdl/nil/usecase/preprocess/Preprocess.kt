package jp.ac.osaka_u.sdl.nil.usecase.preprocess

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import jp.ac.osaka_u.sdl.nil.NILMain.Companion.CODE_BLOCK_FILE_NAME
import jp.ac.osaka_u.sdl.nil.entity.CodeBlock
import jp.ac.osaka_u.sdl.nil.entity.TokenSequence
import jp.ac.osaka_u.sdl.nil.util.parallelIfSpecified
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
