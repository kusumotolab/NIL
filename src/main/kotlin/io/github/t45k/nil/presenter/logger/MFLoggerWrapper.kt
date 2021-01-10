package io.github.t45k.nil.presenter.logger

/**
 * This class is for Mutation Framework (https://github.com/jeffsvajlenko/MutationInjectionFramework).
 * When clone detectors are validated by Mutation framework,
 * the tools' standard output must be only their output file name.
 */
class MFLoggerWrapper(private val outputFileName: String) : LoggerWrapper {
    override fun infoStart() {} // NOP
    override fun infoPreprocessCompletion(size: Int) {} // NOP
    override fun infoInvertedIndexCreationCompletion(partition: Int) {} // NOP
    override fun infoCloneDetectionCompletion(partition: Int) {} // NOP

    override fun infoEnd(time: String) = print(outputFileName)
}
