package io.github.t45k.nil.presentor.logger

class MFLoggerWrapper(private val outputFileName: String) : LoggerWrapper {
    override fun infoStart() {}

    override fun infoPreprocessCompletion(size: Int) {}

    override fun infoPartitionSize(size: Int) {}

    override fun infoInvertedIndexCreationCompletion(partition: Int) {}

    override fun infoCloneDetectionCompletion(partition: Int) {}

    override fun infoEnd(time: String) = print(outputFileName)
}
