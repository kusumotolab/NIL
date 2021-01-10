package io.github.t45k.nil.presenter.logger

interface LoggerWrapper {
    fun infoStart()
    fun infoPreprocessCompletion(size: Int)
    fun infoPartitionSize(size: Int)
    fun infoInvertedIndexCreationCompletion(partition: Int)
    fun infoCloneDetectionCompletion(partition: Int)
    fun infoEnd(time: String)
}
