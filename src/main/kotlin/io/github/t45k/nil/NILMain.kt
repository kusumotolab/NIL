package io.github.t45k.nil

import io.github.t45k.nil.entity.CodeBlock
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import io.github.t45k.nil.output.BigCloneEvalFormat
import io.github.t45k.nil.output.CSV
import io.github.t45k.nil.tokenizer.SymbolSeparator
import io.github.t45k.nil.tokenizer.Tokenizer
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.parallel.ParallelFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.math.min

class NILMain(private val config: NILConfig) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.info("Start")

        val codeBlockFile = File("code_blocks")
        val tokenSequences: List<TokenSequence> = codeBlockFile.bufferedWriter().use { bw ->
            collectSourceFiles(config.src)
                .parallelIfSpecified(config.threads)
                .runOn(Schedulers.io())
                .flatMap { collectBlocks(it) }
                .sequential()
                .doOnEach { it.value?.let { codeBlock -> bw.appendLine(codeBlock.toString()) } }
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

                val startIndex: Int = i * config.partitionSize
                val endOfIndexing = min(startIndex + config.partitionSize, tokenSequences.size)
                for (index in startIndex until endOfIndexing) {
                    location.put(tokenSequences[index].toNgrams(config.gramSize), index)
                }
                logger.info("Partition ${i + 1}: Index creation has been completed.")

                Flowable.range(startIndex, tokenSequences.size - startIndex)
                    .parallelIfSpecified(config.threads)
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
                logger.info("Partition ${i + 1}: Clone Detection in has been completed.")
            }
        }

        val endTime = System.currentTimeMillis()
        logger.info("End")
        logger.info("time: ${(endTime - startTime).toTime()}")

        if (config.isForBigCloneEval) {
            BigCloneEvalFormat()
        } else {
            CSV()
        }.convert(config.outputFileName, codeBlockFile, clonePairFile)
    }

    private fun collectSourceFiles(dir: File): Flowable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toFlowable()

    private fun collectBlocks(sourceFile: File): Flowable<CodeBlock> =
        Flowable.just(sourceFile)
            .flatMap { AST(tokenizer::tokenize, config).extractBlocks(it) }

    private fun <T> Flowable<T>.parallelIfSpecified(threads: Int): ParallelFlowable<T> =
        if (threads > 0) {
            parallel(threads)
        } else {
            parallel()
        }
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
