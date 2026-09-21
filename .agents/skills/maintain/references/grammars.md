# ANTLR grammars-v4 mapping

Load this when replacing `src/main/antlr` from https://github.com/antlr/grammars-v4.

NIL flattens `.g4` files into `src/main/antlr/` and Java support classes into `src/main/java/` (default package). Transformers import them with no package (`import CPP14Lexer`, …).

## Lineage map (PR #73)

| Language | grammars-v4 directory | Files into `src/main/antlr/` | Java bases into `src/main/java/` |
|---|---|---|---|
| C++ | `cpp/` | `CPP14Lexer.g4`, `CPP14Parser.g4` | `cpp/Java/CPP14ParserBase.java` |
| C# | `csharp/v7/` | `CSharpLexer.g4`, `CSharpParser.g4` | `csharp/v7/Java/CSharpLexerBase.java`, `CSharpParserBase.java` |
| Kotlin | `kotlin/kotlin/` | `KotlinLexer.g4`, `KotlinParser.g4`, `UnicodeClasses.g4` | none |
| Python | `python/python3_14/` | `PythonLexer.g4`, `PythonParser.g4` | `python/python3_14/Java/PythonLexerBase.java` |
| PHP | `php/` | `PhpLexer.g4`, `PhpParser.g4` | `php/Java/PhpLexerBase.java` |
| Go | `golang/` | `GoLexer.g4`, `GoParser.g4` | `golang/Java/GoParserBase.java` |
| JavaScript | `javascript/javascript/` | `JavaScriptLexer.g4`, `JavaScriptParser.g4` | `javascript/javascript/Java/JavaScriptLexerBase.java`, `JavaScriptParserBase.java` |
| TypeScript | `javascript/typescript/` | `TypeScriptLexer.g4`, `TypeScriptParser.g4` | `javascript/typescript/Java/TypeScriptLexerBase.java`, `TypeScriptParserBase.java` |
| Rust | `rust/` | `RustLexer.g4`, `RustParser.g4` | `rust/Java/RustLexerBase.java`, `RustParserBase.java` |
| Swift | `swift/swift5/` | `Swift5Lexer.g4`, `Swift5Parser.g4` | `swift/swift5/Java/SwiftSupport.java`, `SwiftSupportLexer.java` |

`UnicodeClasses.g4` is imported by `KotlinLexer.g4` — copy it together.

Python: `python3_14` is the Robert Einhorn successor of the previous grammar. If a later `python3_XX` directory of the **same lineage** appears, prefer it over `python/python`.

## Do not use (different grammars; break transformers)

| Directory | Why |
|---|---|
| `csharp/v8-spec` | Rewrite, not the v7 ECMA-334 lineage |
| `kotlin/kotlin-formal` | Different Kotlin grammar |
| `python/python` | Different Python grammar (not Einhorn) |
| `javascript/ecmascript` | Combined ECMAScript grammar, not the JavaScript lexer+parser pair |
| `javascript/jsx` | JSX rewrite, not `javascript/javascript` |
| `swift/swift2`, `swift/swift3` | Older Swift grammars, not the swift5 lineage |

Do not copy non-Java target runtimes (C#, Python, Go, …) from grammars-v4.

Java clone analysis uses Eclipse JDT (`JavaTransformer.kt` / `JavaLexer.kt`), not ANTLR. Do not add a Java grammar.

## Transformer contracts

If a same-lineage update renames a rule, change only the matching call. If the names below disappear because the **lineage** changed, you picked the wrong directory.

| Transformer | Listener entry | Root rule |
|---|---|---|
| `CPPTransformer.kt` | `enterFunctionDefinition` / `FunctionDefinitionContext` | `translationUnit()` |
| `CSharpTransformer.kt` | `enterMethod_declaration` / `Method_declarationContext` | `compilation_unit()` |
| `KotlinTransformer.kt` | `enterFunctionDeclaration` / `FunctionDeclarationContext` | `kotlinFile()` |
| `PythonTransformer.kt` | `enterFunction_def` / `Function_defContext` | `file_input()` |
| `PhpTransformer.kt` | `enterFunctionDeclaration` / `FunctionDeclarationContext` (and class methods via `enterClassStatement` when `methodBody()` is present) | `htmlDocument()` |
| `GoTransformer.kt` | `enterFunctionDecl` / `FunctionDeclContext` and `enterMethodDecl` / `MethodDeclContext` (when `block()` is present) | `sourceFile()` |
| `JavaScriptTransformer.kt` | `enterFunctionDeclaration` / `FunctionDeclarationContext`; `enterMethodDefinition` / `MethodDefinitionContext` | `program()` |
| `TypeScriptTransformer.kt` | `enterFunctionDeclaration` / `FunctionDeclarationContext` and `enterMethodDeclarationExpression` / `MethodDeclarationExpressionContext` (when `functionBody()` is present) | `program()` |
| `RustTransformer.kt` | `enterFunction_` / `Function_Context` (when `blockExpression()` is present) | `crate()` |
| `SwiftTransformer.kt` | `enterFunction_declaration` / `Function_declarationContext` (when `function_body()` is present) | `top_level()` |

## generateGrammarSource

```kotlin
tasks.generateGrammarSource {
    // Comment out when generating lexers and parsers from g4 file
    enabled = false
    outputDirectory = file("$projectDir/src/main/java")
}
```

Toggle `enabled` to `true` only for `./gradlew generateGrammarSource`, then set it back to `false`. Commit the regenerated Java (including `.interp` / `.tokens`) with the `.g4` files and copied `*Base.java`.

## Last-known grammar refresh

- PR #73 (`update-antlr`), grammars-v4 `e356c2f`, 2026-09-21
- Earlier grammar release: v2.0.0 / PR #69
