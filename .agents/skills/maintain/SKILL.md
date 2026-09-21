---
name: maintain
description: Maintain NIL by updating Gradle/Kotlin/Java dependencies, raising the project JDK and Java clone-analysis target to the latest LTS, opening and merging a GitHub PR into master, and publishing a patch GitHub release with NIL-all.jar. Use when asked to update deps, bump JDK/LTS, create/merge a maintenance PR, or cut a patch release.
---

# Maintain NIL

End-to-end maintenance playbook captured from the dependency + JDK 25 session (PR #71, release v2.0.1).

Use this skill when the user asks to update dependencies, bump the JDK/LTS (project **and** analysis target), open/merge a maintenance PR, or publish a patch release. If they ask for the full maintenance pass, run every phase in order. If they ask for a subset, run only that phase but keep later phases available.

## Pipeline

1. **Dependencies** — bump plugins, libraries, and the Gradle wrapper.
2. **JDK LTS** — project toolchain **and** Java clone-analysis target, plus docs.
3. **Verify** — `./gradlew test shadowJar` on the target JDK.
4. **PR and merge** — branch, commit, PR into `master`, merge commit.
5. **Patch release** — tag `vX.Y.Z+1`, attach `NIL-all.jar`.

Load `references/file-map.md` for exact files, last-known versions, and lookup sources.

## Key principles

- Versions live in `build.gradle.kts` and `gradle/wrapper/gradle-wrapper.properties`. `settings.gradle.kts` is only `rootProject.name = "NIL"` — do not add versions there.
- There is **no** version catalog and **no** product-version file. Git tags (`vMAJOR.MINOR.PATCH`) are the release version.
- Default branch is `master`, not `main`.
- Routine dep/JDK maintenance is a **patch** bump (v2.0.0 → v2.0.1).
- Merge with a **merge commit**, matching PRs #65, #67, #69, #71. Do not squash.
- The Java **analysis** target (JDT JLS / scanner / compiler source) must stay in lockstep with the project JDK LTS.
- Do not regenerate or edit ANTLR output in `src/main/java`. Leave `generateGrammarSource` disabled. Leave `code_blocks` and `clone_pairs` untracked.

## Phase 1 — Update dependencies

Edit `build.gradle.kts` plugin and library versions. Look up current latest on Maven Central / Gradle Plugin Portal; do not invent versions.

| Coordinate | Notes |
|---|---|
| `kotlin("jvm")` | Kotlin 2.x JVM plugin |
| `id("com.gradleup.shadow")` | Successor of `com.github.johnrengelman.shadow`. Do **not** revert to johnrengelman. |
| `io.reactivex.rxjava3:rxkotlin` | May already be latest |
| `ch.qos.logback:logback-classic` | |
| `org.eclipse.jdt:org.eclipse.jdt.core` | Must support the analysis JLS level |
| `org.antlr:antlr4` | Keep `generateGrammarSource` disabled; bump the `antlr(...)` dep only |
| `kotlin-test` / `kotlin-test-junit` | No explicit version — comes from the Kotlin plugin |

Gradle wrapper (`gradle/wrapper/gradle-wrapper.properties`):

- Bump when a plugin requires it (Shadow 9 needs Gradle 9) or when a newer Gradle is needed to run on the target JDK (Gradle 8.12 cannot run on JDK 25).
- Prefer `./gradlew wrapper --gradle-version <version>` so wrapper files stay consistent.

Skip `settings.gradle.kts` unless versioned dependencies appear there.

## Phase 2 — Latest LTS JDK (project + analysis target)

When the user asks to update the JDK, or a new LTS exists, update **both** the build JDK and the Java language NIL can parse.

Project JDK in `build.gradle.kts`:

```kotlin
java {
    sourceCompatibility = JavaVersion.VERSION_<N>
    targetCompatibility = JavaVersion.VERSION_<N>
}
```

Analysis target (must match `<N>`):

- `JavaTransformer.kt` — `import org.eclipse.jdt.core.dom.AST.JLS<N>`, `ASTParser.newParser(JLS<N>)`, `JavaCore.COMPILER_SOURCE` `"<N>"`
- `JavaLexer.kt` — `ToolFactory.createScanner(false, false, false, "<N>")`

Docs (`JDK <N>+`): `README.md`, `AGENTS.md`, `REQUIREMENTS.md`.

If JDT has no `JLS<N>` yet, bump `org.eclipse.jdt.core` first. Do not leave project JDK and analysis JLS on different majors.

## Phase 3 — Verify

Run on the **target** JDK, not an older one:

```bash
java -version    # confirm the LTS
./gradlew test shadowJar
```

`shadowJar` may warn about duplicate Kotlin module-metadata files; that does not fail the build. Do not “fix” it unless the task fails.

Requires JDK 25+ (see `AGENTS.md`). Do not enable `generateGrammarSource`.

## Phase 4 — PR and merge

Only when the user asks to create/merge a PR (or as part of the full pass).

1. Branch from up-to-date `master`. Historical name: `update_deps` (also `update-deps`).
2. Commit the unstaged maintenance work. Style from this repo: `update deps and target Java 25`. When the agent authors the commit, add `--trailer "Co-authored-by: Junie <junie@jetbrains.com>"`.
3. Push and open a PR into `master`.
   - Title pattern: `Update dependencies and JDK <N>` (or `Update dependencies` if JDK did not change).
   - Body: summary bullets of version changes + test plan with `./gradlew test shadowJar` on the target JDK.
4. Merge with a merge commit (`gh pr merge <n> --merge`). Do not squash or rebase-merge.

## Phase 5 — Patch GitHub release

Only after the PR is merged to `master`, and only when the user asks to release (or as part of the full pass).

1. Fast-forward local `master` to `origin/master`.
2. Read the latest `v*` tag (`gh release list` / `git tag -l 'v*' --sort=-v:refname`). Increment **patch**: `v2.0.0` → `v2.0.1`.
3. `./gradlew shadowJar` and attach `./build/libs/NIL-all.jar` (not `NIL.jar`).
4. Publish:

```bash
gh release create vX.Y.Z \
  --title "<same as PR title>" \
  --target master \
  --notes "$(cat <<'EOF'
- Use latest Gradle, Java, Kotlin, etc.
- Update Java version of NIL can handle (<old> -> <new>)
EOF
)" \
  ./build/libs/NIL-all.jar
```

Omit the Java-version bullet if the analysis/project JDK did not change. Match the tone of v1.2.0 / v2.0.1; do not invent a changelog file.

5. Verify with `gh release view vX.Y.Z` (Latest, asset present, not draft/prerelease).

## MUST NOT

- Do not edit or regenerate ANTLR sources under `src/main/java`.
- Do not enable `tasks.generateGrammarSource`.
- Do not commit `code_blocks` or `clone_pairs`.
- Do not guess library/plugin versions; look them up.
- Do not bump only `sourceCompatibility` and leave JDT `JLS*`, `COMPILER_SOURCE`, and the Java scanner on the old level.
- Do not use a major/minor tag bump for this maintenance path unless the user explicitly asks.
- Do not squash-merge maintenance PRs.

## Checklist

See `checklists/maintain.md`.
