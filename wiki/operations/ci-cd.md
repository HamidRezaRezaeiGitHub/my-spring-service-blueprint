---
title: CI and CD
domain: operations
tags: [ci, docker, deployment, security]
status: current
last_updated: 2026-08-17
---

# CI and CD

CI runs the full Docker-backed Maven test suite, including ArchUnit boundaries, packages the application, discovers the resulting executable JAR without hard-coded filenames, lints the tracked wiki, and builds the container image. Requirement workspaces remain ignored local planning artifacts and are linted only during local work. Dependency vulnerability thresholds run when Maven dependencies change and on the weekly schedule. Dependabot covers Maven, Docker, and GitHub Actions; the Maven wrapper verifies its downloaded distribution checksum.

Deployment is an optional Google Cloud Run adapter. DEV builds one image tagged with the tested commit SHA. UAT promotes that existing immutable tag rather than rebuilding it. All project, region, repository, service, identity, database, and secret names come from GitHub Environment variables.

The application artifact remains provider-neutral; a different deployment platform can consume the same image without source changes.

The optional DEV workflow rebuilds the already-tested commit before pushing it, while UAT/production promote that DEV image tag. A deployment that requires bit-for-bit proof between CI and DEV should move registry publication into CI and promote an image digest with signing/attestation; that requires repository-specific registry credentials and policy and is intentionally left to the adopter.
