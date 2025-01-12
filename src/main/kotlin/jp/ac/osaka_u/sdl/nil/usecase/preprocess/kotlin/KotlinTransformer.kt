package jp.ac.osaka_u.sdl.nil.usecase.preprocess.kotlin

import KotlinLexer
import KotlinParser
import KotlinParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class KotlinTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::KotlinLexer,
        ::KotlinParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : KotlinParserBaseListener() {
            override fun enterFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext) =
                action(ctx)
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as KotlinParser).kotlinFile()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
