package jp.ac.osaka_u.sdl.nil.presenter.output

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
