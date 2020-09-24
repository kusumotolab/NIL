package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.github.t45k.lvmapper.entity.TokenSequence
import io.github.t45k.lvmapper.tokenizer.LexicalAnalyzer
import io.github.t45k.lvmapper.tokenizer.SymbolSeparator
import io.github.t45k.lvmapper.tokenizer.Tokenizer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
class LVMapperMain(private val config: LVMapperConfig) {

    private val tokenizer: Tokenizer =
        when (config.tokenizeMethod) {
            TokenizeMethod.LEXICAL_ANALYSIS -> LexicalAnalyzer()
            TokenizeMethod.SYMBOL_SEPARATION -> SymbolSeparator()
        }

    fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .filter { it.tokenSequence.size in 20..2_000 }
            .toList()
            .blockingGet()

        println("${codeBlocks.size} code blocks have been extracted.\n")

        val location = Location()
        val verification = Verification(codeBlocks)
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

        val endTime = System.currentTimeMillis()

        println(clonePairs.size)
        println("time: ${(endTime - startTime) / 1000} seconds")
    }

    // TODO use rolling hash
    private fun createSeed(tokenSequence: TokenSequence): List<Int> =
        (0..(tokenSequence.size - config.windowSize))
            .map { tokenSequence.subList(it, it + config.windowSize).hashCode() }
            .distinct()

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun collectSourceFiles(dir: Path): Observable<Path> =
        Files.walk(dir)
            .asSequence()
            .filter { it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourcePath: Path): Observable<CodeBlock> =
        Observable.just(sourcePath)
            .flatMap { AST(tokenizer::tokenize).extractBlocks(it).toObservable() }
}

fun main(args: Array<String>) {
    val config: LVMapperConfig = parseArgs(args)
    LVMapperMain(config).run()
}
