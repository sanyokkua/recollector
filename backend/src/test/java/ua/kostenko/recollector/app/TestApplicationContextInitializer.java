package ua.kostenko.recollector.app;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class TestApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres").withInitScript(
            "liquibase-latest-schema.sql");

    static {
        postgres.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        var propUrl = "spring.datasource.url=" + postgres.getJdbcUrl();
        var propUsername = "spring.datasource.username=" + postgres.getUsername();
        var propPassword = "spring.datasource.password=" + postgres.getPassword();

        TestPropertyValues.of(propUrl, propUsername, propPassword).applyTo(applicationContext.getEnvironment());
    }
}
