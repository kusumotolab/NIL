# Add-language checklist

Work top to bottom.

## Research

- [ ] Language is not already in `Language` / README
- [ ] Not Java (JDT, not ANTLR)
- [ ] grammars-v4 directory has a Java lexer+parser (not a rewrite / non-Java runtime)
- [ ] Recorded short SHA
- [ ] Identified start rule, function/method definition rules, comment tokens, `superClass` / imports

## Intake

- [ ] Copied `.g4` (and imported grammars) into `src/main/antlr/`
- [ ] Copied required `*Base.java` into `src/main/java/` with default package
- [ ] Did not copy non-Java runtimes
- [ ] Enabled `generateGrammarSource`, ran `./gradlew generateGrammarSource`, set `enabled = false`
- [ ] Generated lexer/parser/listener/`.interp`/`.tokens` present; not hand-edited

## Wire

- [ ] `Language` enum value
- [ ] `toLangOrException()` alias(es)
- [ ] `PreprocessFactory` arm (exhaustive `when`)
- [ ] `*Preprocess` collects the right extensions
- [ ] `*Transformer` visitor = functions/methods only (method-body gate if needed)
- [ ] Root rule is the parser start rule
- [ ] `isNegligible` matches this language's comments/whitespace (not C++'s `false` by default)

## Prove

- [ ] `FizzBuzz.<ext>` with two near-duplicate functions and moved comments
- [ ] Each function ≥ 6 lines / 50 tokens
- [ ] `NILMainTest.test<Lang>` with `-bce -t 1 -p 1 -l <alias>` and trailing `""`
- [ ] README Languages row

## Maps

- [ ] `maintain/references/grammars.md` lineage + transformer contract
- [ ] `maintain/references/file-map.md` generated prefix set
- [ ] maintain SKILL.md Phase 3 language list if it names languages

## Verify

- [ ] `./gradlew test --tests jp.ac.osaka_u.sdl.nil.NILMainTest.test<Lang>`
- [ ] `./gradlew test`
- [ ] `generateGrammarSource` is disabled
- [ ] Did not commit `code_blocks` / `clone_pairs`
