---
name: Agent Team Design Decisions
description: The rationale and boundaries for each agent in the dextea-v2-api agent team, established 2026-04-12
type: project
---

Agent team finalized on 2026-04-12 with the following 5 agents:

**business-code-developer** (purple): Implements Controller/Service/DTO/Converter/ErrorCode for Dextea Spring Boot services. Explicitly bound to Dextea Backend Spec. Tools: Glob, Grep, Read, Edit, Write, Bash. Previously lacked `tools` field and referenced wrong multi-language stack.

**db-schema-architect** (blue): Designs DDL under `src/main/resources/sql/`, MyBatis-Plus entities, and Mapper interfaces. Previously had wrong DDL path (`resource/sql/` instead of `src/main/resources/sql/`) and no mention of MyBatis-Plus annotations.

**microservice-architect** (yellow): Handles cross-service concerns: service decomposition, Feign client design, caching architecture (Caffeine L1 + Redis L2), common module utilities. Scope narrowed from original (removed OAuth2/RBAC/Kafka which are not used in this project). Previously lacked `tools` field.

**code-review-expert** (red): Reads actual code files and produces Chinese review reports. Added `tools: Glob, Grep, Read, Bash` so it can actually read files. Added Dextea Backend Spec compliance as an explicit review dimension.

**test-engineer** (green): NEW agent added to fill the gap — writes JUnit 5 + Mockito unit tests for Service layer and MockMvc slice tests for Controller layer. No test directory currently exists in the project.

**Why:** The original team had tools missing from most agents, wrong project-specific paths, overly broad scope in microservice-architect, and no test agent despite zero test coverage.

**How to apply:** When orchestrating agents, always check the boundary table in tech-director.md to pick the right agent. test-engineer is the right choice whenever someone asks about test coverage, TDD, or writing test classes.
