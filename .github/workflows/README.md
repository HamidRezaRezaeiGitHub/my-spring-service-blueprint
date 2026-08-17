# Workflows

- `ci.yml` runs the PostgreSQL-backed test suite and architecture rules, packages the executable JAR, lints the tracked wiki, and builds the credential-free image.
- `security.yml` fails when OWASP Dependency Check reaches the configured CVSS threshold; it runs for Maven changes and on the weekly schedule.
- `deploy-dev.yml` is an opt-in template that builds one commit-tagged image from a successful CI commit and deploys it to DEV.
- `deploy-uat.yml` is an opt-in template that promotes that existing image tag to UAT or production without rebuilding.

Deployment is an optional Google Cloud Run adapter and is disabled by default. After configuring GCP, set the repository variable `GCP_DEPLOYMENT_ENABLED=true` to opt in. Until then, automatic DEV runs and manually dispatched promotions are skipped safely.

Configure every resource through GitHub Environment variables: `WORKLOAD_IDENTITY_PROVIDER`, `DEPLOY_SERVICE_ACCOUNT`, `ARTIFACT_REGISTRY_HOST`, `ARTIFACT_PROJECT_ID`, `ARTIFACT_REPOSITORY`, `CONTAINER_IMAGE`, `GCP_PROJECT_ID`, `GCP_REGION`, `CLOUD_RUN_SERVICE`, `CLOUD_RUN_RUNTIME_SERVICE_ACCOUNT`, `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD_SECRET`. Keep the artifact coordinates identical across environments so promotion resolves the image published by DEV; `ARTIFACT_PROJECT_ID` is intentionally separate from each Cloud Run target's `GCP_PROJECT_ID`. The deployment identity needs the appropriate Artifact Registry and Cloud Run permissions, each target's Cloud Run service agent needs access to the shared image, and the runtime identity needs Secret Manager access. The workflows validate these variables before attempting authentication, image publication, or deployment.

Optional Firebase/OpenAI provider secrets are intentionally not assumed by these generic workflows. Add environment-specific mounts or variables only in the owning GitHub Environment and keep the application image unchanged.
