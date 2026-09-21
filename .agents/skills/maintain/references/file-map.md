# File map and last-known versions

Load this when updating versions or hunting JDK/analysis touchpoints.

## Versioned files

| File | What to bump |
|---|---|
| `build.gradle.kts` | Kotlin JVM plugin, Shadow plugin, RxKotlin, Logback, Eclipse JDT Core, ANTLR |
| `gradle/wrapper/gradle-wrapper.properties` | `distributionUrl` Gradle version |
| `settings.gradle.kts` | Nothing (name only) |

`kotlin-test` / `kotlin-test-junit` have no explicit versions.

## JDK / analysis-target files

| File | What to change together |
|---|---|
| `build.gradle.kts` | `JavaVersion.VERSION_<N>` for source and target |
| `src/main/kotlin/jp/ac/osaka_u/sdl/nil/usecase/preprocess/java/JavaTransformer.kt` | `AST.JLS<N>`, `ASTParser.newParser(JLS<N>)`, `COMPILER_SOURCE` `"<N>"` |
| `src/main/kotlin/jp/ac/osaka_u/sdl/nil/usecase/preprocess/java/JavaLexer.kt` | `ToolFactory.createScanner(..., "<N>")` |
| `README.md` | Requirements: `JDK <N>+` |
| `AGENTS.md` | Setup: `Requires JDK <N>+` |
| `REQUIREMENTS.md` | `- JDK <N>+` |

## Do not touch

- `src/main/java/**` — generated ANTLR lexers/parsers (`generateGrammarSource` is disabled)
- `src/main/antlr/*.g4` — unless the user asked to update language grammars (that is a different release, e.g. PR #69 / v2.0.0)
- `code_blocks`, `clone_pairs` — generated runtime output, keep untracked

## Lookup sources

- Gradle plugins: https://plugins.gradle.org (`org.jetbrains.kotlin.jvm`, `com.gradleup.shadow`)
- Libraries: Maven Central (`rxkotlin`, `logback-classic`, `org.eclipse.jdt.core`, `antlr4`)
- Gradle wrapper: https://gradle.org/releases/
- Java LTS: current LTS as of the 2026-09 session is **25**. Confirm before bumping past 25.
- Shadow: plugin id is `com.gradleup.shadow` (Gradleup fork). `com.github.johnrengelman.shadow` is retired.

## Last-known-good (PR #71 / v2.0.1, 2026-09-21)

Use as a baseline, not a ceiling. Prefer newer when looking up.

| Component | Version |
|---|---|
| Kotlin JVM plugin | 2.4.20 |
| Shadow (`com.gradleup.shadow`) | 9.6.1 |
| Gradle wrapper | 9.7.1 |
| Logback classic | 1.6.3 |
| Eclipse JDT Core | 3.47.0 |
| RxKotlin | 3.0.1 |
| ANTLR 4 | 4.13.2 |
| Project + analysis JDK | 25 (JLS25, compiler/scanner `"25"`) |
| Product tag | v2.0.1 |

## Historical release pattern

| Tag | Title | Notes |
|---|---|---|
| v1.2.0 | Update dependencies | Java 11 → 21; JAR asset `NIL-all.jar` |
| v2.0.0 | Update ANTLR and language definition files | Grammar work, not this skill |
| v2.0.1 | Update dependencies and JDK 25 | Java 21 → 25; patch after #71 |

Release notes bullets used for dep/JDK releases:

```
- Use latest Gradle, Java, Kotlin, etc.
- Update Java version of NIL can handle (<old> -> <new>)
```

## GitHub conventions

- Repo: `kusumotolab/NIL`, default branch `master`
- Maintenance branch names seen: `update_deps`, `update-deps`
- PR merge method: merge commit (`gh pr merge --merge`)
- Release asset: `./build/libs/NIL-all.jar` from `./gradlew shadowJar`
- Commit trailer when the agent commits: `Co-authored-by: Junie <junie@jetbrains.com>`
