---
title: CI and CD
domain: operations
tags: [ci, docker, deployment, security]
status: current
last_updated: 2026-08-17
---

# CI and CD

CI runs the full Docker-backed Maven test suite, including ArchUnit boundaries, packages the application, discovers the resulting executable JAR without hard-coded filenames, lints the tracked wiki, and builds the container image. Requirement workspaces remain ignored local planning artifacts and are linted only during local work. Pinned OWASP Dependency Check runs when Maven dependencies change and on the weekly schedule. The optional `NVD_API_KEY` repository secret enables NVD's higher request limit; when it is absent, the workflow omits API-key configuration and uses the slower public rate limit. Dependabot covers Maven, Docker, and GitHub Actions; the Maven wrapper verifies its downloaded distribution checksum.

Deployment is an optional Google Cloud Run adapter and is disabled by default. Both deployment jobs require the repository variable `GCP_DEPLOYMENT_ENABLED=true`; without it, successful CI runs and manual dispatches skip safely. Once enabled, each job validates its GitHub Environment variables before authenticating to GCP. DEV builds one image tagged with the tested commit SHA. UAT promotes that existing commit tag rather than rebuilding it. `ARTIFACT_PROJECT_ID` is separate from the Cloud Run target's `GCP_PROJECT_ID`; keep the artifact host, project, repository, and image name identical across environments so every target resolves the image published by DEV. `DEPLOY_SERVICE_ACCOUNT` is the Workload Identity deployment principal, while `CLOUD_RUN_RUNTIME_SERVICE_ACCOUNT` is explicitly attached to the service and needs Secret Manager access. For a shared cross-project registry, each target's Cloud Run service agent also needs image-read access. All project, region, repository, service, identity, database, and secret names come from GitHub Environment variables.

The application artifact remains provider-neutral; a different deployment platform can consume the same image without source changes.

The optional DEV workflow rebuilds the already-tested commit before pushing it, while UAT/production promote that DEV image tag. A deployment that requires bit-for-bit proof between CI and DEV should move registry publication into CI and promote an image digest with signing/attestation; that requires repository-specific registry credentials and policy and is intentionally left to the adopter.
