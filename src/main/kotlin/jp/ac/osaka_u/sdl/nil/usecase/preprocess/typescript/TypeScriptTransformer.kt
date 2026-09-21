package jp.ac.osaka_u.sdl.nil.usecase.preprocess.typescript

import TypeScriptLexer
import TypeScriptParser
import TypeScriptParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class TypeScriptTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::TypeScriptLexer,
        ::TypeScriptParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : TypeScriptParserBaseListener() {
            override fun enterFunctionDeclaration(ctx: TypeScriptParser.FunctionDeclarationContext) {
                if (ctx.functionBody() != null) {
                    action(ctx)
                }
            }

            override fun enterMethodDeclarationExpression(ctx: TypeScriptParser.MethodDeclarationExpressionContext) {
                if (ctx.functionBody() != null) {
                    action(ctx)
                }
            }
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as TypeScriptParser).program()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
