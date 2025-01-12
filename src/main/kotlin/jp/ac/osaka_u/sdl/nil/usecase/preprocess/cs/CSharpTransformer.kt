package jp.ac.osaka_u.sdl.nil.usecase.preprocess.cs

import CSharpLexer
import CSharpParser
import CSharpParserBaseListener
import jp.ac.osaka_u.sdl.nil.NILConfig
import jp.ac.osaka_u.sdl.nil.usecase.preprocess.AntlrTransformer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeListener

class CSharpTransformer(config: NILConfig) :
    AntlrTransformer(
        config,
        ::CSharpLexer,
        ::CSharpParser
    ) {
    override fun createVisitor(action: (ParserRuleContext) -> Unit): ParseTreeListener =
        object : CSharpParserBaseListener() {
            override fun enterMethod_declaration(ctx: CSharpParser.Method_declarationContext) =
                action(ctx)
        }

    override fun Parser.extractRuleContext(): ParserRuleContext =
        (this as CSharpParser).compilation_unit()

    override fun Token.isNegligible(): Boolean =
        this.text.run {
            this[0] == '\n' || this[0] == ' ' || this[0] == '\r' ||
                this.isEmpty() || this.startsWith("//") || this.startsWith("/*")
        }
}
