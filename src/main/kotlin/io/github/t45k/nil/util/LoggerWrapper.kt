package io.github.t45k.nil.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggerWrapper(private val isForMutationInjectionFramework: Boolean, clazz: Class<*>) {
    private val logger: Logger = LoggerFactory.getLogger(clazz)

    fun info(message:String) {
        if(!isForMutationInjectionFramework){
            logger.info(message)
        }
    }
}
