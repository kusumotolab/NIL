package jp.ac.osaka_u.sdl.nil.usecase.preprocess.rust

import RustLexer
import RustParser
import RustParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class RustTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::RustLexer,
        ::RustParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : RustParserBaseListener() {
            override fun enterFunction_(ctx: RustParser.Function_Context) {
                if (ctx.blockExpression() != null) {
                    action(ctx)
                }
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as RustParser).crate()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
