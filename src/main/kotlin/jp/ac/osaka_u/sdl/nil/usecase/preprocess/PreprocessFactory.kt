package jp.ac.osaka_u.sdl.nil.usecase.preprocess

import jp.ac.osaka_u.sdl.nil.Language
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp.CPPPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp.CPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cs.CSharpPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.java.JavaPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.kotlin.KotlinPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.python.PythonPreprocess

class PreprocessFactory {
    companion object {
        fun create(config: NILConfig): Preprocess =
            when (config.lang) {
                Language.JAVA -> JavaPreprocess(config)
                Language.C -> CPreprocess(config)
                Language.CPP -> CPPPreprocess(config)
                Language.CS -> CSharpPreprocess(config)
                Language.PYTHON -> PythonPreprocess(config)
                Language.KOTLIN -> KotlinPreprocess(config)
            }
    }
}
