package io.github.t45k.nil.presenter.output

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