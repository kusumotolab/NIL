# Maintain checklist

Work top to bottom. Skip a section only when the user asked for a subset.

## Dependencies

- [ ] Looked up latest versions (do not guess)
- [ ] Updated plugins/libraries in `build.gradle.kts`
- [ ] Shadow still uses `com.gradleup.shadow` (not johnrengelman)
- [ ] Wrapper bumped if Shadow/JDK requires a newer Gradle
- [ ] Left `settings.gradle.kts` unchanged unless it gained versions
- [ ] Left `generateGrammarSource` disabled (regeneration is the grammar phase)

## JDK LTS (if bumping Java)

- [ ] `sourceCompatibility` / `targetCompatibility` = latest LTS
- [ ] `JavaTransformer.kt`: `JLS<N>` import + parser + `COMPILER_SOURCE`
- [ ] `JavaLexer.kt`: scanner source `"<N>"`
- [ ] JDT Core version supports that JLS
- [ ] `README.md`, `AGENTS.md`, `REQUIREMENTS.md` say `JDK <N>+`

## ANTLR grammars (if updating language definitions)

- [ ] Fetched latest [antlr/grammars-v4](https://github.com/antlr/grammars-v4) and recorded the short SHA
- [ ] Copied matching-lineage `.g4` files into `src/main/antlr/` (see `references/grammars.md`)
- [ ] C# still `csharp/v7/` (not `v8-spec`); Kotlin still `kotlin/kotlin/`; Python still Einhorn `python3_XX/` (not `python/python`)
- [ ] Copied required `*Base.java` into `src/main/java/`; removed unused bases
- [ ] Enabled `generateGrammarSource`, ran `./gradlew generateGrammarSource`, set `enabled = false` again
- [ ] Transformers still match listener/root-rule contracts (or were updated in-lineage)
- [ ] Did not add a Java ANTLR grammar; did not hand-edit generated lexers/parsers

## Verify

- [ ] `java -version` is the target LTS
- [ ] `./gradlew test shadowJar` succeeded
- [ ] `generateGrammarSource` is disabled
- [ ] Did not commit `code_blocks` / `clone_pairs`

## PR and merge (if asked)

- [ ] Branch from current `master` (`update_deps` / `update-antlr`)
- [ ] Commit message matches repo style; Junie co-author trailer if agent committed
- [ ] PR into `master` with summary + `./gradlew test shadowJar` test plan
- [ ] Merged with a **merge commit** (not squash)
- [ ] PR shows `MERGED`; `origin/master` includes the merge

## Patch release (if asked, after merge)

- [ ] Local `master` fast-forwarded to `origin/master`
- [ ] Tag is previous `vX.Y.Z` with **patch + 1**
- [ ] Title matches the PR; notes follow v1.2.0 / v2.0.1 (ANTLR bullet only if grammars changed)
- [ ] Asset is `NIL-all.jar` (rebuilt on merged master)
- [ ] `gh release view` shows Latest, published, asset present
