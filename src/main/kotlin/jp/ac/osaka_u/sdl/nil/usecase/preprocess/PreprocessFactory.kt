package jp.ac.osaka_u.sdl.nil.usecase.preprocess

import jp.ac.osaka_u.sdl.nil.Language
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp.CPPPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp.CPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.cs.CSharpPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.go.GoPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.java.JavaPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.javascript.JavaScriptPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.kotlin.KotlinPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.php.PhpPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.python.PythonPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.rust.RustPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.swift.SwiftPreprocess
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.typescript.TypeScriptPreprocess

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
                Language.PHP -> PhpPreprocess(config)
                Language.GO -> GoPreprocess(config)
                Language.JAVASCRIPT -> JavaScriptPreprocess(config)
                Language.TYPESCRIPT -> TypeScriptPreprocess(config)
                Language.RUST -> RustPreprocess(config)
                Language.SWIFT -> SwiftPreprocess(config)
            }
    }
}
