package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import io.github.t45k.nil.tokenizer.SymbolSeparator
import io.github.t45k.nil.tokenizer.Tokenizer
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.math.min

class NILMain(private val config: NILConfig) {
    private val tokenizer: Tokenizer = SymbolSeparator()
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.info("Start")

        val codeBlockFile = File("code_blocks")
        val tokenSequences: List<TokenSequence> = codeBlockFile.bufferedWriter().use { bw ->
            collectSourceFiles(config.src)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap { collectBlocks(it) }
                .sequential()
                .doOnEach { if (it.value != null) bw.appendLine(reformat(it.value)) }
                .map { it.tokenSequence }
                .toList()
                .blockingGet()
        }
        logger.info("${tokenSequences.size} code blocks have been extracted.")

        val numOfPartitions = (tokenSequences.size + config.partitionSize - 1) / config.partitionSize
        logger.info("Code blocks were divided into $numOfPartitions partitions.")

        val clonePairFile = File("clone_pairs")
        clonePairFile.bufferedWriter().use { bw ->
            val verification = Verification(config, tokenSequences)
            val location = Location(config)
            repeat(numOfPartitions) { i ->
                location.clear()
                logger.info("\nPartition ${i + 1}:")

                val startIndex: Int = i * config.partitionSize
                val endOfIndexing = min(startIndex + config.partitionSize, tokenSequences.size)
                for (index in startIndex until endOfIndexing) {
                    location.put(tokenSequences[index].toNgrams(config.gramSize), index)
                }
                logger.info("Index creation has been completed.")

                Flowable.range(startIndex, tokenSequences.size - startIndex)
                    .parallel()
                    .runOn(Schedulers.computation())
                    .flatMap { index ->
                        val nGrams = tokenSequences[index].toNgrams(config.gramSize)
                        location.locate(nGrams)
                            .toFlowable()
                            .filter { index > it }
                            .filter { verification.verify(index, it) }
                            .map { it to index }
                    }
                    .sequential()
                    .blockingSubscribe { bw.appendLine("${it.first},${it.second}") }
                logger.info("Clone Detection in this partition has been completed.")
            }
        }

        val endTime = System.currentTimeMillis()
        logger.info("End")
        logger.info("time: ${(endTime - startTime).toTime()}")

        File(config.outputFileName).bufferedWriter().use { bw ->
            val codeBlocks: List<String> = codeBlockFile.readLines()
            clonePairFile.bufferedReader().use { br ->
                br.lines()
                    .map { line ->
                        val (id1, id2) = line.split(",")
                        "${codeBlocks[id1.toInt()]},${codeBlocks[id2.toInt()]}"
                    }
                    .forEach { bw.appendLine(it) }
            }
        }
    }

    private fun reformat(codeBlock: CodeBlock): String =
        "${codeBlock.fileName},${codeBlock.startLine},${codeBlock.endLine}"

    private fun collectSourceFiles(dir: File): Flowable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toFlowable()

    private fun collectBlocks(sourceFile: File): Flowable<CodeBlock> =
        Flowable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize, config).extractBlocks(it) }
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
