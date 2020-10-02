package io.github.t45k.tle

import io.github.t45k.tle.entity.CodeBlock
import io.github.t45k.tle.entity.NGrams
import io.github.t45k.tle.output.CSV
import io.github.t45k.tle.tokenizer.SymbolSeparator
import io.github.t45k.tle.tokenizer.Tokenizer
import io.github.t45k.tle.util.ProgressMonitor
import io.github.t45k.tle.util.toTime
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
open class TLEMain(private val config: TLEConfig) {

    private val tokenizer: Tokenizer = SymbolSeparator()

    open fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .toList()
            .blockingGet()

        println("${codeBlocks.size} code blocks have been extracted in ${((System.currentTimeMillis() - startTime) / 1000).toTime()}.\n")

        val location = Location(config.filteringThreshold, codeBlocks)
        val verification = Verification(codeBlocks)
        val progressMonitor = ProgressMonitor(codeBlocks.size)
        var index = 0
        val clonePairs: List<Pair<Int, Int>> = codeBlocks.toObservable()
            .flatMap { codeBlock ->
                val clonePairs: Observable<Pair<Int, Int>> = location.locate(codeBlock.nGrams)
                    .filter { verification.verify(index, it) }
                    .map { index to it }

                location.put(codeBlock.nGrams, index)
                progressMonitor.update(++index)

                clonePairs
            }
            .toList()
            .blockingGet()

        println("${clonePairs.size} clone pairs are detected.")

        val endTime = System.currentTimeMillis()
        println("time: ${((endTime - startTime) / 1000).toTime()}")

        CSV().output(config.outputFileName, clonePairs, codeBlocks)
    }

    // TODO use rolling hash
    private fun createSeed(NGrams: NGrams): List<Int> =
        (0..(NGrams.size - config.gramSize))
            .map { NGrams.subList(it, it + config.gramSize).hashCode() }

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize, config).extractBlocks(it) }
}

fun main(args: Array<String>) {
    val config: TLEConfig = parseArgs(args)
    TLEMain(config).run()
}
