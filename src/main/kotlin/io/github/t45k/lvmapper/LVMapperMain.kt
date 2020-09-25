package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.github.t45k.lvmapper.entity.TokenSequence
import io.github.t45k.lvmapper.output.CSV
import io.github.t45k.lvmapper.tokenizer.LexicalAnalyzer
import io.github.t45k.lvmapper.tokenizer.SymbolSeparator
import io.github.t45k.lvmapper.tokenizer.Tokenizer
import io.github.t45k.lvmapper.util.ProgressMonitor
import io.github.t45k.lvmapper.util.toTime
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
open class LVMapperMain(protected val config: LVMapperConfig) {

    protected val tokenizer: Tokenizer =
        when (config.tokenizeMethod) {
            TokenizeMethod.LEXICAL_ANALYSIS -> LexicalAnalyzer()
            TokenizeMethod.SYMBOL_SEPARATION -> SymbolSeparator()
        }

    open fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .filter { it.tokenSequence.size in config.minToken..config.maxToken }
            .toList()
            .blockingGet()

        println("${codeBlocks.size} code blocks have been extracted in ${((System.currentTimeMillis() - startTime) / 1000).toTime()}.\n")

        val location = Location(config.filteringThreshold)
        val verification = Verification(codeBlocks, config)
        val progressMonitor = ProgressMonitor(codeBlocks.size)
        val clonePairs: List<Pair<Int, Int>> = codeBlocks
            .flatMapIndexed { index, codeBlock ->
                val seeds: List<Int> = createSeed(codeBlock.tokenSequence)
                val clonePairs: List<Pair<Int, Int>> = location.locate(seeds)
                    .filter { verification.verify(index, it) }
                    .map { index to it }

                location.put(seeds, index)
                progressMonitor.update(index + 1)

                clonePairs
            }

        println("${clonePairs.size} clone pairs are detected.")

        val endTime = System.currentTimeMillis()
        println("time: ${((endTime - startTime) / 1000).toTime()}")

        CSV().output(config.outputFileName, clonePairs, codeBlocks)
    }

    // TODO use rolling hash
    protected fun createSeed(tokenSequence: TokenSequence): List<Int> =
        (0..(tokenSequence.size - config.windowSize))
            .map { tokenSequence.subList(it, it + config.windowSize).hashCode() }
            .distinct()

    protected fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    protected fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize).extractBlocks(it).toObservable() }
}

fun main(args: Array<String>) {
    val config: LVMapperConfig = parseArgs(args)
    if (config.isForBenchmark) {
        ForBenchmark(config).run()
    } else {
        LVMapperMain(config).run()
    }
}
