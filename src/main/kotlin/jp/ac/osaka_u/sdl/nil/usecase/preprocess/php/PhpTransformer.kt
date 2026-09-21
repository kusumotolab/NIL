package jp.ac.osaka_u.sdl.nil.usecase.preprocess.php

import PhpLexer
import PhpParser
import PhpParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class PhpTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::PhpLexer,
        ::PhpParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : PhpParserBaseListener() {
            override fun enterFunctionDeclaration(ctx: PhpParser.FunctionDeclarationContext) =
                action(ctx)

            override fun enterClassStatement(ctx: PhpParser.ClassStatementContext) {
                if (ctx.methodBody() != null) {
                    action(ctx)
                }
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as PhpParser).htmlDocument()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*") || this.startsWith("#")
        }
}
