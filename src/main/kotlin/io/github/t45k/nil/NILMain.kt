package io.github.t45k.nil

import io.github.t45k.nil.core.JavaPreprocess
import io.github.t45k.nil.core.Location
import io.github.t45k.nil.core.Verification
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import io.github.t45k.nil.output.BigCloneEvalFormat
import io.github.t45k.nil.output.CSV
import io.github.t45k.nil.util.LoggerWrapper
import io.github.t45k.nil.util.parallelIfSpecified
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NILMain(private val config: NILConfig) {
    private val logger = LoggerWrapper(config.isForMutationInjectionFramework, this.javaClass)
    private val codeBlockFile = File("code_blocks")
    private val clonePairFile = File("clone_pairs")

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.info("Start")

        val tokenSequences: List<TokenSequence> = JavaPreprocess(config).collectTokenSequences(codeBlockFile)
        logger.info("${tokenSequences.size} code blocks have been extracted.")

        val numOfPartitions = (tokenSequences.size + config.partitionSize - 1) / config.partitionSize
        logger.info("Code blocks were divided into $numOfPartitions partitions.")

        val verification = Verification(config)
        repeat(numOfPartitions) { i ->
            val startIndex: Int = i * config.partitionSize

            val location = Location.from(config, tokenSequences, startIndex)
            logger.info("Partition ${i + 1}: Index creation has been completed.")

            clonePairFile.bufferedWriter().use { bw ->
                Flowable.range(startIndex, tokenSequences.size - startIndex)
                    .parallelIfSpecified(config.threads)
                    .runOn(Schedulers.computation())
                    .flatMap { index ->
                        val nGrams = tokenSequences[index].toNgrams(config.gramSize)
                        location.collectCandidates(nGrams)
                            .toFlowable()
                            .filter { index > it }
                            .filter { verification.verify(tokenSequences[index], tokenSequences[it]) }
                            .map { it to index }
                    }
                    .sequential()
                    .blockingSubscribe { bw.appendLine("${it.first},${it.second}") }
                logger.info("Partition ${i + 1}: Clone detection has been completed.")
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

        if (config.isForMutationInjectionFramework) {
            println(config.outputFileName)
        }
    }
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
