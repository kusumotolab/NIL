package io.github.t45k.nil.usecase.preprocess

import io.github.t45k.nil.NILConfig
import io.github.t45k.nil.entity.CodeBlock
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.toFlowable
import java.io.File

class JavaPreprocess(private val config: NILConfig) : Preprocess(config.threads) {
    override fun collectSourceFiles(dir: File): Flowable<File> =
        dir.walk()
            .filter { it.isFile && it.toString().endsWith(".java") }
            .toFlowable()

    override fun collectBlocks(srcFile: File): Flowable<CodeBlock> =
        Flowable.just(srcFile)
            .flatMap { JavaParser(SymbolSeparator.Companion::tokenize, config).extractBlocks(it) }
}
