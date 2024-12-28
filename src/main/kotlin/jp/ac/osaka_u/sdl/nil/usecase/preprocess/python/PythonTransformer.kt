package jp.ac.osaka_u.sdl.nil.usecase.preprocess.python

import PythonLexer
import PythonParser
import PythonParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class PythonTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::PythonLexer,
        ::PythonParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : PythonParserBaseListener() {
            override fun enterFunction_def(ctx: PythonParser.Function_defContext) {
                action(ctx)
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as PythonParser).file_input()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this[0] == '#'
        }
}
