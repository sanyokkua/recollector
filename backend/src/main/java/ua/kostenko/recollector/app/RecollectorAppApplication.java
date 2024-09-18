package ua.kostenko.recollector.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The entry point for the Recollector application.
 * <p>
 * This class is annotated with {@link SpringBootApplication}, which enables auto-configuration, component scanning,
 * and configuration properties for the Spring Boot application.
 * <p>
 * The main method starts the Spring Boot application by calling {@link SpringApplication#run(Class, String[])}.
 */
@SpringBootApplication
public class RecollectorAppApplication {

    /**
     * The main method that serves as the entry point to the application.
     * <p>
     * This method starts the Spring Boot application by calling {@link SpringApplication#run(Class, String[])}.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(RecollectorAppApplication.class, args);
    }
}
