---
title: CI and CD
domain: operations
tags: [ci, docker, deployment, security]
status: current
last_updated: 2026-08-13
---

# CI and CD

CI runs the full Docker-backed Maven test suite, packages the application, discovers the resulting executable JAR without hard-coded filenames, and builds the container image. Dependency vulnerability thresholds fail the security job.

Deployment is an optional Google Cloud Run adapter. DEV builds one image tagged with the tested commit SHA. UAT promotes that existing immutable tag rather than rebuilding it. All project, region, repository, service, identity, database, and secret names come from GitHub Environment variables.

The application artifact remains provider-neutral; a different deployment platform can consume the same image without source changes.
