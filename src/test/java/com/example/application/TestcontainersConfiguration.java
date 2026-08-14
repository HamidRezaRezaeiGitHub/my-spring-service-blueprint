package com.example.application;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Single source of truth for the test database.
 * <p>
 * The container is declared as a {@code static final} field, so a single
 * instance is reused across every Spring test context in the same Surefire JVM.
 * {@link ServiceConnection} hands the container's JDBC URL/credentials to
 * Spring Boot's autoconfigured {@code DataSource}, so no profile needs to
 * configure {@code spring.datasource.*}.
 * <p>
 * Container labels make the running container easy to spot in Docker Desktop
 * or {@code docker ps}: look for {@code dev.application.purpose=test-database}.
 * <p>
 * Docker is therefore a hard requirement for running the test suite.
 * See {@code wiki/guides/testing.md} for the rationale.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // Managed by Testcontainers for the lifetime of the test JVM.
    @SuppressWarnings({"resource", "RedundantSuppression"})
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18-alpine"))
                    .withDatabaseName("application")
                    .withUsername("application")
                    .withPassword("application")
                    .withLabel("dev.application.purpose", "test-database")
                    .withLabel("dev.application.project", "application");

    static {
        POSTGRES.start();
    }

    @Bean(destroyMethod = "")
    @ServiceConnection
    public PostgreSQLContainer postgresContainer() {
        return POSTGRES;
    }
}
