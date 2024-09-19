package ua.kostenko.recollector.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up resource handlers for the OpenAPI documentation.
 * This class implements {@code WebMvcConfigurer} to customize the configuration of the
 * Spring MVC framework.
 * <p>
 * The primary purpose of this configuration is to serve the Swagger UI from the classpath
 * so that it can be accessed via HTTP for API documentation and testing.
 * </p>
 */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    /**
     * Adds resource handlers to serve the Swagger UI from the classpath.
     * <p>
     * This method configures the application to serve static resources for the Swagger UI
     * which is provided by the Springdoc OpenAPI library. The resources are served from
     * the {@code META-INF/resources/webjars/springdoc-openapi-ui/} directory within the
     * classpath.
     * </p>
     *
     * @param registry the {@code ResourceHandlerRegistry} to use for adding resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);
        // Check http://localhost:8080/swagger-ui/index.html for API Description
    }
}
