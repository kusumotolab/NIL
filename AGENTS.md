# AGENTS.md

NIL is a scalable code clone detector using N-gram, inverted index, and LCS.

## Setup commands

- Requires JDK 21+
- Build fat JAR: `./gradlew shadowJar`
- Run: `java -jar ./build/libs/NIL-all.jar -s <src-dir>`
- Tests: `./gradlew test`

## Code style

- Kotlin 2.0 under `jp.ac.osaka_u.sdl.nil`
- Do not regenerate or edit ANTLR output in `src/main/java` (`generateGrammarSource` is disabled)
- Leave generated runtime files `code_blocks` and `clone_pairs` untracked
