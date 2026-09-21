# Grammar intake from antlr/grammars-v4

Load this in Phases 1–2. Source of truth: https://github.com/antlr/grammars-v4.

NIL flattens `.g4` files into `src/main/antlr/` and Java support classes into `src/main/java/` (default package). Transformers import them with no package (`import PhpLexer`, …).

## Fetch (PHP session)

Record the short SHA, then download at that SHA. Do not copy from a floating `master` URL without recording the commit.

```bash
# latest commit touching the language path
curl -fsSL "https://api.github.com/repos/antlr/grammars-v4/commits/master?path=<dir>"

# list Java bases (404 means none)
curl -fsSL "https://api.github.com/repos/antlr/grammars-v4/contents/<dir>/Java?ref=<sha>"

# files
curl -fsSL -o PhpLexer.g4 "https://raw.githubusercontent.com/antlr/grammars-v4/<sha>/<dir>/PhpLexer.g4"
curl -fsSL -o PhpParser.g4 "https://raw.githubusercontent.com/antlr/grammars-v4/<sha>/<dir>/PhpParser.g4"
curl -fsSL -o PhpLexerBase.java "https://raw.githubusercontent.com/antlr/grammars-v4/<sha>/<dir>/Java/PhpLexerBase.java"
```

Prefer the SHA already in `.agents/skills/maintain/references/grammars.md` when it is still the current default-branch tip; otherwise use the latest and record it.

## What to copy

| Kind | Destination | Notes |
|---|---|---|
| `*Lexer.g4`, `*Parser.g4` | `src/main/antlr/` | Flat; Gradle does not use grammars-v4 subdirectories |
| Imported `.g4` | `src/main/antlr/` | e.g. `UnicodeClasses.g4` for Kotlin |
| `Java/*Base.java` | `src/main/java/` | Only if `superClass` / parser base requires it; default package |

Do not copy C#, Python, Go, JavaScript, … target folders.

## Inspect before generate

| Look for | Why |
|---|---|
| Lexer/parser `superClass` | Missing `*Base.java` fails `generateGrammarSource` |
| `tokenVocab` / `import` | Copy imported grammars into the same flat directory |
| First / file-level parser rule | Transformer `extractRuleContext()` (PHP: `htmlDocument()`) |
| `function` / `method` **definition** rules | Visitor `enter*`. Ignore call / lambda rules |
| Comment tokens (`//`, `/*`, `#`, channels) | `Token.isNegligible()` |

If a class-member rule covers fields and methods, plan to gate on a method-body child after generation (PHP: `ClassStatementContext.methodBody() != null`).

## generateGrammarSource

```kotlin
tasks.generateGrammarSource {
    // Comment out when generating lexers and parsers from g4 file
    enabled = false
    outputDirectory = file("$projectDir/src/main/java")
}
```

Toggle `enabled` to `true` only for `./gradlew generateGrammarSource`, then set it back to `false`.

Generated (commit, do not edit): `*Lexer.java`, `*Parser.java`, `*Listener.java`, `*BaseListener.java`, `.interp`, `.tokens`.

Hand-copied (commit, do not regenerate): `*Base.java`.

After generate, confirm visitor names against `*ParserBaseListener` (`enterFunctionDeclaration`, `enterFunction_def`, …). If the names you planned from the `.g4` differ, trust the generated listener.

## Existing lineages (do not switch these)

When adding a **new** language these are irrelevant except as examples of “pick the Java lexer+parser folder, not a rewrite”:

| Language | Directory | Do not use |
|---|---|---|
| C++ | `cpp/` | |
| C# | `csharp/v7/` | `csharp/v8-spec` |
| Kotlin | `kotlin/kotlin/` | `kotlin/kotlin-formal` |
| Python | `python/python3_14/` (Einhorn `python3_XX`) | `python/python` |
| PHP | `php/` | |

Java is JDT, not ANTLR.

## Worked example: PHP at `e356c2f`

- Files: `php/PhpLexer.g4`, `php/PhpParser.g4`, `php/Java/PhpLexerBase.java`
- Start rule: `htmlDocument` (PHP embedded in HTML)
- Functions: `functionDeclaration`
- Methods: `classStatement` with `methodBody`
- Comments: `//`, `/*`, `#` (plus whitespace)
- Not collected: `lambdaFunction`, `arrowFunction`, `functionCall`
