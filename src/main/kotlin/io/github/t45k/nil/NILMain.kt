package io.github.t45k.nil

import io.github.t45k.nil.core.JavaPreprocess
import io.github.t45k.nil.core.Location
import io.github.t45k.nil.core.Verification
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.entity.toNgrams
import io.github.t45k.nil.presentor.logger.LoggerWrapperFactory
import io.github.t45k.nil.presentor.output.BigCloneEvalFormat
import io.github.t45k.nil.presentor.output.CSV
import io.github.t45k.nil.util.parallelIfSpecified
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NILMain(private val config: NILConfig) {
    private val logger =
        LoggerWrapperFactory.create(config.isForMutationInjectionFramework, this.javaClass, config.outputFileName)
    private val codeBlockFile = File("code_blocks")
    private val clonePairFile = File("clone_pairs")

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.infoStart()

        val tokenSequences: List<TokenSequence> = JavaPreprocess(config).collectTokenSequences(codeBlockFile)
        logger.infoPreprocessCompletion(tokenSequences.size)

        val numOfPartitions = (tokenSequences.size + config.partitionSize - 1) / config.partitionSize
        logger.infoPartitionSize(numOfPartitions)

        val verification = Verification(config)
        clonePairFile.bufferedWriter().use { bw ->
            repeat(numOfPartitions) { i ->
                val startIndex: Int = i * config.partitionSize

                val location = Location.from(config, tokenSequences, startIndex)
                logger.infoInvertedIndexCreationCompletion(i + 1)

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
                logger.infoCloneDetectionCompletion(i + 1)
            }
        }
        val endTime = System.currentTimeMillis()
        logger.infoEnd((endTime - startTime).toTime())

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
