package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.output.CSV
import io.github.t45k.nil.tokenizer.SymbolSeparator
import io.github.t45k.nil.tokenizer.Tokenizer
import io.github.t45k.nil.util.ProgressMonitor
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File
import kotlin.math.min

class NILMain(private val config: NILConfig) {
    companion object {
        private const val PARTITION = 500_000
    }

    private val tokenizer: Tokenizer = SymbolSeparator()

    fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .toList()
            .blockingGet()

        println("${codeBlocks.size} code blocks have been extracted in ${((System.currentTimeMillis() - startTime) / 1000).toTime()}")
        println("Code blocks was divided into ${(codeBlocks.size + PARTITION - 1) / PARTITION} partitions")

        val verification = Verification(codeBlocks)
        val location = Location(config.filteringThreshold, codeBlocks)
        val clonePairs: List<Pair<Int, Int>> = generateSequence(0) { it + 1 }
            .takeWhile { it * PARTITION < codeBlocks.size }
            .onEach { println("\nPartition ${it + 1}:") }
            .map { it * PARTITION }
            .flatMap { startIndex ->
                sequence {
                    val endOfIndexing = min(startIndex + PARTITION, codeBlocks.size)
                    val progressMonitor = ProgressMonitor(codeBlocks.size - startIndex)
                    for (index in startIndex until codeBlocks.size) {
                        val nGrams = codeBlocks[index].tokenSequence.toNgrams()
                        location.locate(nGrams,index)
                            .filter { verification.verify(index, it) }
                            .forEach { yield(it to index) }

                        if (index < endOfIndexing) {
                            location.put(nGrams, index)
                        }
                        progressMonitor.update(index - startIndex + 1)
                    }

                    location.clear()
                }
            }.toList()

        println("${clonePairs.size} clone pairs are detected.")

        val endTime = System.currentTimeMillis()
        println("time: ${((endTime - startTime) / 1000).toTime()}")

        CSV().output(config.outputFileName, clonePairs, codeBlocks)
    }

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize, config).extractBlocks(it) }

    // TODO: Use rolling hash
    private fun TokenSequence.toNgrams(): List<Int> =
        (0..(this.size - config.gramSize))
            .map { this.subList(it, it + config.gramSize).hashCode() }
            .distinct()
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
