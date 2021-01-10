package io.github.t45k.nil

import io.github.t45k.nil.entity.HuntSzymanskiLCS
import io.github.t45k.nil.entity.InvertedIndex
import io.github.t45k.nil.entity.TokenSequence
import io.github.t45k.nil.presenter.logger.LoggerWrapperFactory
import io.github.t45k.nil.presenter.output.FormatFactory
import io.github.t45k.nil.usecase.CloneDetection
import io.github.t45k.nil.usecase.NGramBasedFilter
import io.github.t45k.nil.usecase.JavaPreprocess
import io.github.t45k.nil.usecase.NGramBasedLocation
import io.github.t45k.nil.usecase.LCSBasedVerification
import io.github.t45k.nil.util.parallelIfSpecified
import io.github.t45k.nil.util.toTime
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class NILMain(private val config: NILConfig) {
    companion object {
        const val CODE_BLOCK_FILE_NAME = "code_blocks"
        const val CLONE_PAIR_FILE_NAME = "clone_pairs"
    }

    private val logger =
        LoggerWrapperFactory.create(config.isForMutationInjectionFramework, this.javaClass, config.outputFileName)

    fun run() {
        val startTime = System.currentTimeMillis()
        logger.infoStart()

        val tokenSequences: List<TokenSequence> = JavaPreprocess(config).collectTokenSequences(config.src)
        logger.infoPreprocessCompletion(tokenSequences.size)

        val numOfPartitions = (tokenSequences.size + config.partitionSize - 1) / config.partitionSize
        logger.infoPartitionSize(numOfPartitions)

        val filteringPhase = NGramBasedFilter(config.filteringThreshold)
        val verifyingPhase = LCSBasedVerification(HuntSzymanskiLCS(), config.verifyingThreshold)

        File(CLONE_PAIR_FILE_NAME).bufferedWriter().use { bw ->
            repeat(numOfPartitions) { i ->
                val startIndex: Int = i * config.partitionSize

                val invertedIndex =
                    InvertedIndex.create(config.partitionSize, config.gramSize, tokenSequences, startIndex)
                logger.infoInvertedIndexCreationCompletion(i + 1)

                val locatingPhase = NGramBasedLocation(invertedIndex)
                val cloneDetection =
                    CloneDetection(locatingPhase, filteringPhase, verifyingPhase, tokenSequences, config.gramSize)
                Flowable.range(startIndex, tokenSequences.size - startIndex)
                    .parallelIfSpecified(config.threads)
                    .runOn(Schedulers.computation())
                    .flatMap { cloneDetection.exec(it) }
                    .sequential()
                    .blockingSubscribe { bw.appendLine("${it.first},${it.second}") }
                logger.infoCloneDetectionCompletion(i + 1)
            }
        }
        val endTime = System.currentTimeMillis()
        logger.infoEnd((endTime - startTime).toTime())

        FormatFactory.create(config.isForBigCloneEval)
            .convert(config.outputFileName)
    }
}

fun main(args: Array<String>) {
    val config: NILConfig = parseArgs(args)
    NILMain(config).run()
}
