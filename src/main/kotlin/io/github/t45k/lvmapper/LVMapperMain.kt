package io.github.t45k.lvmapper

import io.github.t45k.lvmapper.entity.CodeBlock
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.toObservable
import java.io.File
import kotlin.math.absoluteValue

// 一旦リストに保持する
// スケーラビリティを考えると将来的にDBを使うかも
// IDはリストとかDBのインデックスで大丈夫そう
class LVMapperMain(private val config: LVMapperConfig) {
    fun run() {
        val statementHashes: MutableList<List<Int>> = mutableListOf()
        val seeds: MutableList<Map<Int, Int>> = mutableListOf()
        val hashTable: MutableMap<Int, List<Int>> = mutableMapOf()
        collectSourceFiles(config.dir)
            .flatMap(this::collectBlocks)
            .toList()
            .blockingGet()
            .asSequence()
            .mapIndexed { index, codeBlock ->
                val statements: List<Int> = prettyPrint(codeBlock.text)
                statementHashes.add(statements)
                val seed: Map<Int, Int> = (0..(statements.size - 3))
                    .groupingBy { statements.subList(it, it + 3).hashCode().absoluteValue }
                    .eachCount()
                    .toMap()
                val candidates: List<Int> = seed.keys.flatMap { hashTable[it] ?: emptyList() }
                    .distinct()
                    .toList()

                // TODO
                seeds[index] = seed
            }
    }

    private fun collectSourceFiles(dir: File): Observable<File> =
        dir.walk()
            .filter { it.isFile && it.endsWith(".java") }
            .toObservable()

    private fun collectBlocks(sourceFile: File): Observable<CodeBlock> =
        Observable.just(sourceFile)
            .flatMap { AST(it).extractBlocks().toObservable() }
}

fun main(args: Array<String>) {
    val config: LVMapperConfig = parseArgs(args)
    LVMapperMain(config).run()
}
