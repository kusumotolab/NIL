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

`UnicodeClasses.g4` is imported by `KotlinLexer.g4` — copy it together.

Python: `python3_14` is the Robert Einhorn successor of the previous grammar. If a later `python3_XX` directory of the **same lineage** appears, prefer it over `python/python`.

## Do not use (different grammars; break transformers)

| Directory | Why |
|---|---|
| `csharp/v8-spec` | Rewrite, not the v7 ECMA-334 lineage |
| `kotlin/kotlin-formal` | Different Kotlin grammar |
| `python/python` | Different Python grammar (not Einhorn) |

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
