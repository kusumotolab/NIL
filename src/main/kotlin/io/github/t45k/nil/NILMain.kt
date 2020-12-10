package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.NGrams
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.output.CSV
import io.github.t45k.nil.tokenizer.SymbolSeparator
import io.github.t45k.nil.tokenizer.Tokenizer
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
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap { collectBlocks(it).subscribeOn(Schedulers.io()) }
            .toList()
            .blockingGet()
        println("${codeBlocks.size} code blocks have been extracted in ${((System.currentTimeMillis() - startTime) / 1000).toTime()}")
        println("Code blocks was divided into ${(codeBlocks.size + config.partitionSize - 1) / config.partitionSize} partitions")

        val verification = Verification(config, codeBlocks)
        val location = Location(config)
        val clonePairs: List<Pair<Int, Int>> = generateSequence(0) { it + 1 }
            .takeWhile { it * config.partitionSize < codeBlocks.size }
            .onEach { println("\nPartition ${it + 1}:") }
            .map { it * config.partitionSize }
            .flatMap { startIndex ->
                location.clear()
                val endOfIndexing = min(startIndex + config.partitionSize, codeBlocks.size)
                for (index in startIndex until endOfIndexing) {
                    location.put(codeBlocks[index].tokenSequence.toNgrams(), index)
                }

                Observable.fromIterable(startIndex until codeBlocks.size)
                    .flatMap { index ->
                        Observable.just(index)
                            .observeOn(Schedulers.computation())
                            .flatMap {
                                val nGrams = codeBlocks[index].tokenSequence.toNgrams()
                                location.locate(nGrams)
                                    .filter { verification.verify(index, it) }
                                    .map { it to index }
                                    .toObservable()
                            }
                    }
                    .toList()
                    .blockingGet()
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

    private fun TokenSequence.toNgrams(): NGrams =
        (0..(this.size - config.gramSize))
            .map { this.subList(it, it + config.gramSize).hashCode() }
            .distinct()
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
