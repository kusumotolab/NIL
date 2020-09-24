package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.github.t45k.lvmapper.entity.TokenSequence
import io.github.t45k.lvmapper.tokenizer.LexicalAnalyzer
import io.github.t45k.lvmapper.tokenizer.SymbolSeparator
import io.github.t45k.lvmapper.tokenizer.Tokenizer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File

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
            .toList()
            .blockingGet()

        val location = Location()
        val verification = Verification(codeBlocks)
        val clonePairs: List<Pair<Int, Int>> = codeBlocks
            .flatMapIndexed { index, codeBlock ->
                val seeds: List<Int> = createSeed(codeBlock.tokenSequence)
                val clonePairs: List<Pair<Int, Int>> = location.locate(seeds)
                    .filter { verification.verify(index, it) }
                    .map { index to it }

                location.put(seeds, index)

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

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap {
                try {
                    AST(tokenizer::tokenize).extractBlocks(it).toObservable()
                } catch (e: Exception) {
                    System.err.println("hogehoge")
                    Observable.empty()
                }
            }
}

fun main(args: Array<String>) {
    val config: LVMapperConfig = parseArgs(args)
    LVMapperMain(config).run()
}
