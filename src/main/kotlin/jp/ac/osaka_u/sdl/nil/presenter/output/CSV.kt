package jp.ac.osaka_u.sdl.nil.presenter.output

/**
 * NIL's standard output format is "/path/to/file1,start_line1,end_line1,/path/to/file2,start_line2,end_line2",
 * as well as CCAligner.
 */
class CSV : Format() {
    override fun reformat(codeBlock1: String, codeBlock2: String): String =
        "$codeBlock1,$codeBlock2"
}
