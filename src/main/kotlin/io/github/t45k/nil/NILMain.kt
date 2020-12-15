package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.NGrams
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.tokenizer.SymbolSeparator
import io.github.t45k.nil.tokenizer.Tokenizer
import io.github.t45k.nil.util.ProgressMonitor
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import kotlin.math.min

class NILMain(private val config: NILConfig) {
    private val tokenizer: Tokenizer = SymbolSeparator()

    fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlockWriter = File("code_blocks").bufferedWriter()
        val tokenSequences: List<TokenSequence> = collectSourceFiles(config.src)
            .flatMap { collectBlocks(it).subscribeOn(Schedulers.io()) }
            .doOnEach { if (it.value != null) codeBlockWriter.appendLine(reformat(it.value)) }
            .map { it.tokenSequence }
            .toList()
            .blockingGet()
        codeBlockWriter.close()
        println("${tokenSequences.size} code blocks have been extracted in ${((System.currentTimeMillis() - startTime) / 1000).toTime()}")
        println("Code blocks were divided into ${(tokenSequences.size + config.partitionSize - 1) / config.partitionSize} partitions")

        val verification = Verification(config, tokenSequences)
        val location = Location(config)
        val resultWriter = File(config.outputFileName).bufferedWriter()
        for (i in 0 until (tokenSequences.size + config.partitionSize - 1) / config.partitionSize) {
            println("\nPartition ${i + 1}:")
            val startIndex: Int = i * config.partitionSize
            location.clear()
            val endOfIndexing = min(startIndex + config.partitionSize, tokenSequences.size)
            val progressMonitor = ProgressMonitor(endOfIndexing - startIndex)
            for (index in startIndex until endOfIndexing) {
                location.put(tokenSequences[index].toNgrams(), index)
                progressMonitor.update(index - startIndex + 1)
            }
            println("Index creation has been completed.")

            Observable.range(startIndex, tokenSequences.size - startIndex)
                .flatMap { index ->
                    Observable.just(index)
                        .observeOn(Schedulers.computation())
                        .flatMap {
                            val nGrams = tokenSequences[index].toNgrams()
                            location.locate(nGrams)
                                .toObservable()
                                .filter { index > it }
                                .filter { verification.verify(index, it) }
                                .map { it to index }
                        }
                }
                .blockingSubscribe {
                    resultWriter.appendLine("${it.first},${it.second}")
                }
        }

        resultWriter.close()
        val endTime = System.currentTimeMillis()
        println("time: ${((endTime - startTime) / 1000).toTime()}")
    }

    private fun reformat(codeBlock: CodeBlock): String =
        "${codeBlock.fileName},${codeBlock.startLine},${codeBlock.endLine}"

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize, config).extractBlocks(it) }

    private fun TokenSequence.toNgrams(): NGrams =
        (0..(this.size - config.gramSize))
            .map { this.subList(it, it + config.gramSize).hashCode() }
            .distinct()
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
