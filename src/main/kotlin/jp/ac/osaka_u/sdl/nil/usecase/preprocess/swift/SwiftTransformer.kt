package jp.ac.osaka_u.sdl.nil.usecase.preprocess.swift

import Swift5Lexer
import Swift5Parser
import Swift5ParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class SwiftTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::Swift5Lexer,
        ::Swift5Parser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : Swift5ParserBaseListener() {
            override fun enterFunction_declaration(ctx: Swift5Parser.Function_declarationContext) {
                if (ctx.function_body() != null) {
                    action(ctx)
                }
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as Swift5Parser).top_level()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
