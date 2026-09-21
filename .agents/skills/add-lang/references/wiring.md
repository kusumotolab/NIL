# Language wiring

Load this in Phases 3–5. Mirror `php/` (PHP session) unless the user wants a C-like sibling of an existing grammar.

## Files

| Path | Role |
|---|---|
| `src/main/kotlin/jp/ac/osaka_u/sdl/nil/NILConfig.kt` | `enum class Language` + `String.toLangOrException()` |
| `src/main/kotlin/.../usecase/preprocess/PreprocessFactory.kt` | Exhaustive `when (config.lang)` |
| `src/main/kotlin/.../usecase/preprocess/<pkg>/<Name>Preprocess.kt` | Extensions + `flatMap(<Name>Transformer(config)::extractBlocks)` |
| `src/main/kotlin/.../usecase/preprocess/<pkg>/<Name>Transformer.kt` | Extends `AntlrTransformer` |
| `src/test/resources/examples/FizzBuzz.<ext>` | Two commented-apart functions |
| `src/test/kotlin/.../NILMainTest.kt` | `test<Lang>()` |
| `README.md` | Languages table |
| `.agents/skills/maintain/references/grammars.md` | Lineage + transformer contract |
| `.agents/skills/maintain/references/file-map.md` | Generated prefix glob |

`Preprocess.kt` already documents the extension point. Do not change it.

`NILConfigTest` does not list languages. Skip it unless the user asks.

## Naming

| Piece | Convention | PHP |
|---|---|---|
| Package | short lowercase under `...preprocess.` | `php` (C# is `cs`) |
| Classes | ANTLR-style prefix + `Preprocess` / `Transformer` | `PhpPreprocess`, `PhpTransformer` |
| Enum | `SCREAMING_SNAKE` | `Language.PHP` (C# is `CS`, C++ is `CPP`) |
| CLI | common short name; extra aliases if obvious | `php` (`py`/`python`, `kt`/`kotlin`, `cs`/`csharp`) |
| Extensions | `endsWith(".ext")` like siblings | `.php` (C: `.c`/`.h`, C++: `.cpp`/`.hpp`) |

Imports of generated types have **no package**: `import PhpLexer`, `import PhpParser`, `import PhpParserBaseListener`.

## Preprocess template

Copy `PhpPreprocess.kt`. Filter with `it.toString().endsWith(...)`. Multiple extensions use `||` like `CPreprocess` / `CPPPreprocess`.

C is the only sibling-grammar case: `CPreprocess` calls `CPPTransformer`. Do not add a second transformer for a new extension of an existing parser unless the user asks.

## Transformer template

Copy `PhpTransformer.kt`:

- `AntlrTransformer(config, ::PhpLexer, ::PhpParser)`
- `createVisitor`: anonymous `PhpParserBaseListener` calling `action(ctx)` on function/method enters
- `Parser.extractRuleContext()`: `(this as PhpParser).<startRule>()`
- `Token.isNegligible()`: first-char whitespace / empty / comment prefixes, same shape as Kotlin/C#/Python/PHP (including the existing `this[0]` before `isEmpty()` check — do not “fix”)

### Existing contracts

| Transformer | Listener entry | Root rule | `isNegligible` |
|---|---|---|---|
| `CPPTransformer.kt` | `enterFunctionDefinition` | `translationUnit()` | `false` (do not copy) |
| `CSharpTransformer.kt` | `enterMethod_declaration` | `compilation_unit()` | `//` `/*` + whitespace |
| `KotlinTransformer.kt` | `enterFunctionDeclaration` | `kotlinFile()` | `//` `/*` + whitespace |
| `PythonTransformer.kt` | `enterFunction_def` | `file_input()` | `#` + whitespace |
| `PhpTransformer.kt` | `enterFunctionDeclaration`; `enterClassStatement` if `methodBody() != null` | `htmlDocument()` | `//` `/*` `#` + whitespace |

Java is JDT (`JavaTransformer` / `JavaLexer`), not this table.

## CLI

```kotlin
fun String.toLangOrException(): Language =
    when (this.lowercase()) {
        // ...
        "php" -> Language.PHP
        else -> throw InvalidOptionException("Language $this is invalid.")
    }
```

Do not edit the `InvalidOptionException` help text unless the user asks — it does not list languages today.

## FizzBuzz + test

Fixture: two functions, same logic, comment moved (see `FizzBuzz.php` / `FizzBuzz.kt`). Comments must be a form `isNegligible` drops.

```kotlin
@Test
fun testPHP() {
    val config =
        parseArgs(arrayOf("-s", "./src/test/resources/examples", "-bce", "-t", "1", "-p", "1", "-l", "php"))
    NILMain(config).run()

    val outputFile = File(config.outputFileName)
    val result = listOf(
        "examples,FizzBuzz.php,2,15,examples,FizzBuzz.php,17,30",
        ""
    )

    assertEquals(result.sorted(), outputFile.readText().lines().sorted())
    outputFile.delete()
}
```

Always keep the trailing `""`. `-bce` makes the path `examples,FizzBuzz.<ext>,start,end,...`. `-t 1 -p 1` keeps the run deterministic.

Line numbers are 1-based function spans in the fixture. If unknown, run the test once, read the output file, then set the expected pair — do not drop assertions.

## README

|  Name  |    Option     |   Extension   |
|:------:|:-------------:|:-------------:|
|  PHP   |     `php`     |    `.php`     |

Keep the table alignment used in `README.md`.
