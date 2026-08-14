# Workflows

- `ci.yml` runs the PostgreSQL-backed test suite, packages the executable JAR, lints the wiki, and builds the credential-free image.
- `security.yml` fails when OWASP Dependency Check reaches the configured CVSS threshold.
- `deploy-dev.yml` optionally builds one immutable image from a successful CI commit and deploys it to DEV.
- `deploy-uat.yml` promotes that existing image tag to UAT or production without rebuilding.

Deployment is an optional Google Cloud Run adapter. Configure every resource through GitHub Environment variables: `WORKLOAD_IDENTITY_PROVIDER`, `DEPLOY_SERVICE_ACCOUNT`, `ARTIFACT_REGISTRY_HOST`, `GCP_PROJECT_ID`, `GCP_REGION`, `ARTIFACT_REPOSITORY`, `CONTAINER_IMAGE`, `CLOUD_RUN_SERVICE`, `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD_SECRET`.

Optional Firebase/OpenAI provider secrets are intentionally not assumed by these generic workflows. Add environment-specific mounts or variables only in the owning GitHub Environment and keep the application image unchanged.
