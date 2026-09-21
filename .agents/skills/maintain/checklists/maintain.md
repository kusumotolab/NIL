# Maintain checklist

Work top to bottom. Skip a section only when the user asked for a subset.

## Dependencies

- [ ] Looked up latest versions (do not guess)
- [ ] Updated plugins/libraries in `build.gradle.kts`
- [ ] Shadow still uses `com.gradleup.shadow` (not johnrengelman)
- [ ] Wrapper bumped if Shadow/JDK requires a newer Gradle
- [ ] Left `settings.gradle.kts` unchanged unless it gained versions
- [ ] Left ANTLR-generated `src/main/java` and `generateGrammarSource` disabled

## JDK LTS (if bumping Java)

- [ ] `sourceCompatibility` / `targetCompatibility` = latest LTS
- [ ] `JavaTransformer.kt`: `JLS<N>` import + parser + `COMPILER_SOURCE`
- [ ] `JavaLexer.kt`: scanner source `"<N>"`
- [ ] JDT Core version supports that JLS
- [ ] `README.md`, `AGENTS.md`, `REQUIREMENTS.md` say `JDK <N>+`

## Verify

- [ ] `java -version` is the target LTS
- [ ] `./gradlew test shadowJar` succeeded
- [ ] Did not commit `code_blocks` / `clone_pairs`

## PR and merge (if asked)

- [ ] Branch from current `master` (name like `update_deps`)
- [ ] Commit message matches repo style; Junie co-author trailer if agent committed
- [ ] PR into `master` with summary + `./gradlew test shadowJar` test plan
- [ ] Merged with a **merge commit** (not squash)
- [ ] PR shows `MERGED`; `origin/master` includes the merge

## Patch release (if asked, after merge)

- [ ] Local `master` fast-forwarded to `origin/master`
- [ ] Tag is previous `vX.Y.Z` with **patch + 1**
- [ ] Title matches the PR; notes follow v1.2.0 / v2.0.1
- [ ] Asset is `NIL-all.jar` (rebuilt on merged master)
- [ ] `gh release view` shows Latest, published, asset present
