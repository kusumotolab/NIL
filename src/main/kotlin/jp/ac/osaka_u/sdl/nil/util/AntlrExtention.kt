package jp.ac.osaka_u.sdl.nil.util

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams

fun String.toCharStream(): CharStream = CharStreams.fromString(this)
