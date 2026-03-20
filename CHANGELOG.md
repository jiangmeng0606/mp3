# Changelog for codex branch

## Summary
This branch contains non-functional refactors and documentation enhancements generated and applied via codex-skill.

## Commits
- refactor: improve Mp3Utils readability and comments
- refactor: improve Main.scala readability and comments
- refactor(bin): format and add comments to scripts
- docs: add enhanced README (codex branch)
- chore: remove web/ directory and apply refactors

## Notes
- All changes are formatting/comments/refactor only; behavior preserved.
- Build verified via `./mvnw -DskipTests package` and `mvn test` (no tests present).

## Suggested follow-ups
- Add scalafmt CI check to enforce formatting on PRs
- Add unit tests for Mp3Utils.modify and show
- Consider adding a dry-run flag to `convert` to preview changes
