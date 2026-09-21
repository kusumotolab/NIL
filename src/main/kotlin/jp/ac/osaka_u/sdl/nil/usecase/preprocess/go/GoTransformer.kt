package jp.ac.osaka_u.sdl.nil.usecase.preprocess.go

import GoLexer
import GoParser
import GoParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class GoTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::GoLexer,
        ::GoParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : GoParserBaseListener() {
            override fun enterFunctionDecl(ctx: GoParser.FunctionDeclContext) {
                if (ctx.block() != null) {
                    action(ctx)
                }
            }

            override fun enterMethodDecl(ctx: GoParser.MethodDeclContext) {
                if (ctx.block() != null) {
                    action(ctx)
                }
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as GoParser).sourceFile()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this.isEmpty() || this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.startsWith("//") || this.startsWith("/*")
        }
}
