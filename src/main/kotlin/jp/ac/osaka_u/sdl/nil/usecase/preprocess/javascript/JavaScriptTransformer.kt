package jp.ac.osaka_u.sdl.nil.usecase.preprocess.javascript

import JavaScriptLexer
import JavaScriptParser
import JavaScriptParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class JavaScriptTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::JavaScriptLexer,
        ::JavaScriptParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : JavaScriptParserBaseListener() {
            override fun enterFunctionDeclaration(ctx: JavaScriptParser.FunctionDeclarationContext) =
                action(ctx)

            override fun enterMethodDefinition(ctx: JavaScriptParser.MethodDefinitionContext) =
                action(ctx)
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as JavaScriptParser).program()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
