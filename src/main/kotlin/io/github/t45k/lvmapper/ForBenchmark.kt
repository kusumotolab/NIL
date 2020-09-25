package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.github.t45k.lvmapper.entity.TokenSequence
import io.github.t45k.lvmapper.tokenizer.LexicalAnalyzer
import io.github.t45k.lvmapper.tokenizer.SymbolSeparator
import io.github.t45k.lvmapper.tokenizer.Tokenizer
import io.github.t45k.lvmapper.util.toTime
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File

class ForBenchmark(private val config: LVMapperConfig) {

    private val tokenizer: Tokenizer =
        when (config.tokenizeMethod) {
            TokenizeMethod.LEXICAL_ANALYSIS -> LexicalAnalyzer()
            TokenizeMethod.SYMBOL_SEPARATION -> SymbolSeparator()
        }

    fun run() {
        val startTime = System.currentTimeMillis()

        val codeBlocks: MutableList<CodeBlock> = mutableListOf()
        val location = Location(config.filteringThreshold)
        val verification = Verification(codeBlocks, config)
        val result = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .filter { it.tokenSequence.size in config.minToken..config.maxToken }
            .doOnEach { codeBlocks.add(it.value) }
            .flatMap { codeBlock ->
                val index = codeBlocks.size - 1
                val seeds: List<Int> = createSeed(codeBlock.tokenSequence)
                val clonePairs: List<Pair<Int, Int>> = location.locate(seeds)
                    .filter { verification.verify(index, it) }
                    .map { index to it }
                location.put(seeds, index)
                clonePairs.toObservable()
            }
            .map { "${reformat(codeBlocks[it.first])},${reformat(codeBlocks[it.second])}" }
            .reduce { str1, str2 -> str1 + "\n" + str2 }
            .blockingGet()
        File(config.outputFileName).writeText(result)

        println("time: ${((System.currentTimeMillis() - startTime) / 1000).toTime()}")
    }

    private fun reformat(codeBlock: CodeBlock): String {
        val (dirName, fileName) = codeBlock.fileName.split(File.separator).let { it[it.size - 2] to it.last() }
        return "$dirName,$fileName,${codeBlock.startLine},${codeBlock.endLine}"
    }

    // TODO use rolling hash
    private fun createSeed(tokenSequence: TokenSequence): List<Int> =
        (0..(tokenSequence.size - config.windowSize))
            .map { tokenSequence.subList(it, it + config.windowSize).hashCode() }
            .distinct()

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize).extractBlocks(it).toObservable() }
}