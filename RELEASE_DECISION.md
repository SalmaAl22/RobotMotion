# Release Gate Policy and Decision

Date: 2026-03-02
Project: `coen6761:robotmotion`
Scope for gate: `coen6761/Robot` (JaCoCo `CLASS` rule)

## Finalized JaCoCo Release Thresholds

- LINE covered ratio >= `0.95`
- INSTRUCTION covered ratio >= `0.95`
- BRANCH covered ratio >= `0.85`
- METHOD covered ratio >= `0.95`
- COMPLEXITY covered ratio >= `0.85`

## Clean Coverage Pass Results (Robot)

- LINE: `75/75 = 1.000000`
- INSTRUCTION: `292/292 = 1.000000`
- BRANCH: `40/45 = 0.888889`
- METHOD: `9/9 = 1.000000`
- COMPLEXITY: `31/36 = 0.861111`

## Release Decision

`GO` for release from a coverage-gate perspective.

Rationale:
- All five finalized thresholds are met in a clean `mvn clean verify` pass.
- JaCoCo check returns: `All coverage checks have been met.`
