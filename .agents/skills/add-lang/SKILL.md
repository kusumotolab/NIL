---
name: add-lang
description: Add a new clone-detection target language to NIL from antlr/grammars-v4 — copy lexer/parser grammars and Java *Base classes, generate ANTLR Java sources, wire Language/CLI/PreprocessFactory, add Preprocess+Transformer, a FizzBuzz example test, README, and maintain-skill grammar maps. Use when asked to add a language, add a parser from grammars-v4, or support a new -l value. Do not use for refreshing existing grammars (that is the maintain skill) or for Java (Eclipse JDT, not ANTLR).
---

# Add a NIL language

Playbook captured from adding PHP (`antlr/grammars-v4` `php/` at `e356c2f`).

Use this skill when the user asks to add a language, add a parser from [antlr/grammars-v4](https://github.com/antlr/grammars-v4), or support a new `-l` value. If they name a language, run every phase. Do **not** use this skill to refresh grammars already in the tree — that is `maintain`.

Load `references/grammar-intake.md` before copying files. Load `references/wiring.md` before Kotlin/README edits.

## Pipeline

1. **Research** — find the grammars-v4 directory, SHA, `.g4` files, Java bases, start rule, function/method rules, comment tokens.
2. **Intake** — copy grammars and `*Base.java`, generate Java lexer/parser, disable `generateGrammarSource`.
3. **Wire** — `Language` enum, CLI aliases, `PreprocessFactory`, `*Preprocess`, `*Transformer`.
4. **Prove** — FizzBuzz fixture, `NILMainTest`, README Languages row.
5. **Maps** — record the language in the maintain skill so later grammar refreshes include it.
6. **Verify** — targeted test, then `./gradlew test`. Leave `generateGrammarSource` disabled.

PR, merge, and release only if the user asks.

## Key principles

- Clone units are **functions and methods**, not files, classes, calls, or lambdas. Match existing ANTLR languages.
- Grammars come from https://github.com/antlr/grammars-v4. Flatten `.g4` into `src/main/antlr/` and Java bases into `src/main/java/` (default package).
- Generated ANTLR Java is committed; do not hand-edit it. Toggle `generateGrammarSource` on only while generating.
- Java clone analysis uses Eclipse JDT (`JavaTransformer.kt` / `JavaLexer.kt`). Never add a Java ANTLR grammar.
- C reuses `CPPTransformer` with different extensions. Only share a transformer when the user wants a sibling of an existing grammar.
- Leave `code_blocks` and `clone_pairs` untracked.
- Mirror `PhpPreprocess` / `PhpTransformer` and siblings. Do not invent a new preprocess style.

## Phase 1 — Research

Confirm the language is not already in `Language` / README. If it is, stop.

Locate the grammars-v4 folder (PHP was `php/`). Prefer the directory that has a **Java** target (`Java/` and/or `language: Java` in the grammar). If several folders exist, pick the lineage that is a lexer+parser pair with Java support — not a rewrite. Record the short SHA of the default branch (`master`).

Download and inspect before copying into the repo:

- Lexer/parser `options { superClass = ...; tokenVocab = ...; }`
- `import` of other `.g4` files (Kotlin needs `UnicodeClasses.g4`)
- Parser **start rule** (file / compilation unit / host document — PHP is `htmlDocument`, not `phpBlock`)
- Rules for **function and method definitions** (not calls)
- Comment/whitespace lexer rules (for `isNegligible`)

Do not copy non-Java runtimes (C#, Python, Go, …). If there is no usable Java grammar, stop and say so.

## Phase 2 — Intake grammar

Load `references/grammar-intake.md`.

1. Copy `.g4` files into `src/main/antlr/` (flat).
2. Copy required `*Base.java` from that grammar's `Java/` folder into `src/main/java/`. Keep the **default package** (no `package` declaration), matching `PhpLexerBase.java`.
3. In `build.gradle.kts`, set `tasks.generateGrammarSource { enabled = true }` (keep `outputDirectory = file("$projectDir/src/main/java")`). Run:

```bash
./gradlew generateGrammarSource
```

Then set `enabled = false` again. Do not leave the task enabled.

4. Commit the regenerated `*Lexer.java`, `*Parser.java`, `*Listener.java`, `*BaseListener.java`, `.interp`, and `.tokens` together with the `.g4` files and copied bases. Do not hand-edit them.

If generation fails, the grammar is missing a `superClass` base or an imported `.g4`. Fix intake; do not patch generated Java.

## Phase 3 — Wire

Load `references/wiring.md`. Touch only:

| File | Change |
|---|---|
| `NILConfig.kt` | `Language` enum + `toLangOrException()` aliases |
| `PreprocessFactory.kt` | `when` arm |
| `usecase/preprocess/<pkg>/*Preprocess.kt` | extensions + transformer |
| `usecase/preprocess/<pkg>/*Transformer.kt` | listener, root rule, `isNegligible` |

`when (config.lang)` must stay exhaustive.

Visitor: `enter*` on named function/method rules. If methods share a broader member rule, gate on a body child (PHP: `enterClassStatement` when `methodBody() != null`). Do **not** collect lambdas, arrow functions, or whole classes unless that is the language's only function form.

Root rule: the parser start rule after generation (`htmlDocument()`, `file_input()`, `kotlinFile()`, …).

`isNegligible`: whitespace + comments, copied from the closest sibling (`KotlinTransformer` / `CSharpTransformer` for `//` `/*`; `PythonTransformer` for `#`; PHP also `#`). Do not copy `CPPTransformer`'s `false` unless this lexer already drops comments.

## Phase 4 — Fixture, test, README

Add `src/test/resources/examples/FizzBuzz.<ext>` with two near-duplicate functions (`main1` / `main2`) and a comment in a **different** place in each, so Type-3 clones still match after `isNegligible`. Each function must be ≥ default `minLine` (6) and `minToken` (50).

Add `NILMainTest.test<Lang>` using `-bce -t 1 -p 1 -l <alias>`. Expected pair uses BigCloneEval columns and includes a trailing `""` for the final newline — copy `testPHP`. Line numbers are the function spans in the fixture (PHP: `2,15` and `17,30`). If they are wrong, read the output file; do not weaken the assertion.

Add a row to the README **Languages** table (`Name`, `Option`, `Extension`).

## Phase 5 — Maintain-skill maps

Update so the next grammar refresh includes this language:

- `.agents/skills/maintain/references/grammars.md` — lineage map + transformer contract
- `.agents/skills/maintain/references/file-map.md` — generated Java prefix set (`{...,Php,...}`)
- `.agents/skills/maintain/SKILL.md` Phase 3 language list, if it names languages

## Phase 6 — Verify

```bash
./gradlew test --tests jp.ac.osaka_u.sdl.nil.NILMainTest.test<Lang>
./gradlew test
```

`generateGrammarSource` must be `enabled = false`. Do not commit `code_blocks` or `clone_pairs`.

If the test finds **no** clones: visitor too narrow, fixture too small, or `isNegligible` wrong. If it finds **extra** clones: visitor too broad (member rule without a method-body gate).

## MUST NOT

- Do not add a Java ANTLR grammar (Java uses JDT).
- Do not hand-edit generated lexers/parsers/listeners under `src/main/java`.
- Do not leave `tasks.generateGrammarSource` enabled.
- Do not copy non-Java grammars-v4 runtimes.
- Do not collect files, classes, calls, or lambdas as clone units.
- Do not invent a new preprocess/transformer style.
- Do not commit `code_blocks` or `clone_pairs`.
- Do not open a PR or cut a release unless the user asks.

## Checklist

See `checklists/add-lang.md`.
