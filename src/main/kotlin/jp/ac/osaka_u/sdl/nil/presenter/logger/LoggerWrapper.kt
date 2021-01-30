package jp.ac.osaka_u.sdl.nil.presenter.logger

interface LoggerWrapper {
    fun infoStart()
    fun infoPreprocessCompletion(size: Int)
    fun infoInvertedIndexCreationCompletion(partition: Int)
    fun infoCloneDetectionCompletion(partition: Int)
    fun infoEnd(time: String)
}
