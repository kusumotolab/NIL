package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File
import kotlin.math.max
import kotlin.math.min

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
class LVMapperMain(private val config: LVMapperConfig) {
    companion object {
        private const val WINDOW_SIZE = 3
        private const val FILTERING_THRESHOLD = 70
    }

    fun run() {
        val startTime = System.currentTimeMillis()
        val codeBlocks: List<CodeBlock> = collectSourceFiles(config.src)
            .flatMap(this::collectBlocks)
            .toList()
            .blockingGet()
            .asSequence()
            .onEach { it.prettyPrint = format(it.text) }
            .filter { it.prettyPrint.size >= 6 }
            .toList()

        val hashTable: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        val clonePairs: List<Pair<Int, Int>> = codeBlocks
            .flatMapIndexed { index, codeBlock ->
                val seedsFrequency: Map<Int, Int> = createSeed(codeBlock.prettyPrint)
                val clonePairs: List<Pair<Int, Int>> =
                    locate(seedsFrequency.keys.toList(), hashTable)
                        .filter { verify(index, it, codeBlocks) }
                        .map { index to it }

                seedsFrequency.keys.forEach { hashTable.getOrPut(it) { mutableListOf() }.add(index) }
                clonePairs
            }

        val endTime = System.currentTimeMillis()

        println(clonePairs.size)
        println("time: ${(endTime - startTime) / 1000} seconds")
    }

    /*
    TODO
    currently using naive LCS
     */
    private fun verify(id1: Int, id2: Int, codeBlocks: List<CodeBlock>): Boolean {
        val representation1 = codeBlocks[id1].prettyPrint
        val size1 = representation1.size
        val representation2 = codeBlocks[id2].prettyPrint
        val size2 = representation2.size
        val dpTable: Array<Array<Int>> = Array(size1 + 1) { Array(size2 + 1) { 0 } }
        for (i in 1..size1) {
            for (j in 1..size2) {
                if (representation1[i - 1] == representation2[j - 1]) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1
                } else {
                    dpTable[i][j] = max(dpTable[i - 1][j], dpTable[i][j - 1])
                }
            }
        }

        val min = min(size1, size2)
        return dpTable[size1][size2] * 100 / min >= calcVerifyingThreshold(min)
    }

    private fun calcVerifyingThreshold(size: Int) =
        when {
            size <= 10 -> 70
            size >= 20 -> 40
            else -> -3 * size + 100
        }

    private fun locate(seed: List<Int>, hashtable: Map<Int, List<Int>>): List<Int> =
        seed.flatMap { hashtable[it] ?: emptyList() }
            .onEach { }
            .distinct()
            .toList()

    // TODO use rolling hash
    private fun createSeed(prettyPrint: List<Int>): Map<Int, Int> =
        (0..(prettyPrint.size - WINDOW_SIZE))
            .groupingBy { prettyPrint.subList(it, it + WINDOW_SIZE).hashCode() }
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
