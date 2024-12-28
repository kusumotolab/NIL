package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cpp

import CPP14Lexer
import CPP14Parser
import CPP14ParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class CPPTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::CPP14Lexer,
        ::CPP14Parser,
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : CPP14ParserBaseListener() {
            override fun enterFunctionDefinition(ctx: CPP14Parser.FunctionDefinitionContext) =
                action(ctx)
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as CPP14Parser).translationUnit()

    override fun Token.isNegligible(): Boolean = false
}
