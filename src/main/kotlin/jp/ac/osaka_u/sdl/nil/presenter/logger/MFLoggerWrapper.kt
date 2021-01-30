package jp.ac.osaka_u.sdl.nil.presenter.logger

/**
 * This class is for Mutation Framework (https://github.com/jeffsvajlenko/MutationInjectionFramework).
 * When clone detectors are validated by Mutation framework,
 * the tools' standard output must be only their output file name.
 */
class MFLoggerWrapper(private val outputFileName: String) : LoggerWrapper {
    override fun infoStart() {} /* no-op */
    override fun infoPreprocessCompletion(size: Int) {} /* no-op */
    override fun infoInvertedIndexCreationCompletion(partition: Int) {} /* no-op */
    override fun infoCloneDetectionCompletion(partition: Int) {} /* no-op */

    override fun infoEnd(time: String) = print(outputFileName)
}
