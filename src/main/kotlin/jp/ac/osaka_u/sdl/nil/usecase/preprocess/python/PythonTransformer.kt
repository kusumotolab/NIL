package jp.ac.osaka_u.sdl.nil.usecase.preprocess.python

import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.parser.cpp.CPP14Parser
import jp.ac.osaka_u.sdl.nil.parser.cpp.CPP14ParserBaseListener
import jp.ac.osaka_u.sdl.nil.parser.python.Python3BaseListener
import jp.ac.osaka_u.sdl.nil.parser.python.Python3Lexer
import jp.ac.osaka_u.sdl.nil.parser.python.Python3Parser
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class PythonTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::Python3Lexer,
        ::Python3Parser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : Python3BaseListener() {
            override fun enterFuncdef(ctx: Python3Parser.FuncdefContext) =
                action(ctx)
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as Python3Parser).file_input()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this[0] == '#'
        }
}
