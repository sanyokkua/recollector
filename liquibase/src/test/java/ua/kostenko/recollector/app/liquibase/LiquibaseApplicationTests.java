package ua.kostenko.recollector.app.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(initializers = {TestApplicationContextInitializer.class})
class LiquibaseApplicationTests {

    @Test
    void contextLoads() {
        // Just testing the context can be up and running
    }

}
