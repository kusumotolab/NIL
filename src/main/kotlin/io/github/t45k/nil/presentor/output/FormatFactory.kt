package io.github.t45k.nil.presentor.output

class FormatFactory {
    companion object {
        fun create(isForBigCloneEval: Boolean): Format =
            if (isForBigCloneEval) {
                BCEFormat()
            } else {
                CSV()
            }
    }
}