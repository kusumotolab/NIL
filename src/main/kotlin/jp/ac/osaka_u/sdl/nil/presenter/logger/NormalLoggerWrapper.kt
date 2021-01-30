package jp.ac.osaka_u.sdl.nil.presenter.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NormalLoggerWrapper(clazz: Class<*>) : LoggerWrapper {
    private val logger: Logger = LoggerFactory.getLogger(clazz)

    override fun infoStart() =
        logger.info("Start")

    override fun infoPreprocessCompletion(size: Int) =
        logger.info("$size code blocks have been extracted.")

    override fun infoInvertedIndexCreationCompletion(partition: Int) =
        logger.info("Partition $partition: Index creation has been completed.")

    override fun infoCloneDetectionCompletion(partition: Int) =
        logger.info("Partition $partition: Clone detection has been completed.")

    override fun infoEnd(time: String) {
        logger.info("End")
        logger.info("time: $time")
    }
}
