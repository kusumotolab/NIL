package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
class LVMapperMain(private val config: LVMapperConfig) {
    companion object {
        private const val WINDOW_SIZE = 3
        private const val FILTERING_THRESHOLD = 0.7
    }

    fun run() {
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.dir)
            .flatMap(this::collectBlocks)
            .toList()
            .blockingGet()
            .asSequence()
            .onEach { it.prettyPrint = format(it.text) }
            .filter { it.prettyPrint.size >= 6 }
            .toList()

        val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        val seedsFrequencyStore: MutableList<Map<Int, Int>> = mutableListOf()
        val clonePairs = codeBlocks
            .flatMapIndexed { index, codeBlock ->
                val seedsFrequency: Map<Int, Int> = createSeed(codeBlock.prettyPrint)
                val clonePairs: List<Pair<Int, Int>> =
                    locate(seedsFrequency.keys.toList(), hashTable)
                        .filter {
                            isSharingSeeds(
                                seedsFrequency,
                                seedsFrequencyStore[it],
                                chooseDenominator(index, it, codeBlocks)
                            )
                        }
                        .filter { verify(index, it, codeBlocks) }
                        .map { index to it }

                seedsFrequencyStore.add(seedsFrequency)
                seedsFrequency.keys.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(index) }
                return@flatMapIndexed clonePairs
            }
            .map { codeBlocks[it.first].text to codeBlocks[it.second].text }

        println()
    }

    /*
    TODO
    currently using naive LCS
     */
    private fun verify(id1: Int, id2: Int, tokenizedCodeBlocks: List<CodeBlock>): Boolean {
        val representation1 = tokenizedCodeBlocks[id1].prettyPrint
        val representation2 = tokenizedCodeBlocks[id2].prettyPrint

        val dpTable: Array<Array<Int>> = Array(representation1.size + 1) { Array(representation2.size + 1) { 0 } }
        for (i in 1..representation1.size) {
            for (j in 1..representation2.size) {
                if (representation1[i - 1] == representation2[j - 1]) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1
                } else {
                    dpTable[i][j] = max(dpTable[i - 1][j], dpTable[i][j - 1])
                }
            }
        }

        return dpTable[representation1.size][representation2.size].toDouble() / min(
            representation1.size,
            representation2.size
        ).toDouble() >= 0.7
    }

    private fun chooseDenominator(id1: Int, id2: Int, tokenizedCodeBlocks: List<CodeBlock>): Int =
        min(tokenizedCodeBlocks[id1].prettyPrint.size, tokenizedCodeBlocks[id2].prettyPrint.size) - WINDOW_SIZE + 1

    private fun isSharingSeeds(
        seedsFrequencyA: Map<Int, Int>,
        seedsFrequencyB: Map<Int, Int>,
        denominator: Int
    ): Boolean =
        seedsFrequencyA.map { (seed, frequency) ->
            min(frequency, seedsFrequencyB[seed] ?: 0)
        }.sum().toDouble() / denominator.toDouble() >= FILTERING_THRESHOLD

    private fun locate(seed: List<Int>, hashtable: Map<Int, List<Int>>): List<Int> =
        seed.flatMap { hashtable[it] ?: emptyList() }
            .onEach { }
            .distinct()
            .toList()

    // TODO use rolling hash
    private fun createSeed(prettyPrint: List<Int>): Map<Int, Int> =
        (0..(prettyPrint.size - WINDOW_SIZE))
            .groupingBy { prettyPrint.subList(it, it + WINDOW_SIZE).hashCode().absoluteValue }
            .eachCount()
            .toMap()

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap {
                try {
                    AST(it).extractBlocks().toObservable()
                } catch (e: Exception) {
                    Observable.empty()
                }
            }
}

fun main(args: Array<String>) {
    val config: LVMapperConfig = parseArgs(args)
    LVMapperMain(config).run()
}
