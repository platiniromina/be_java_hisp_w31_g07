package com.mercadolibre.be_java_hisp_w31_g07.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    /**
     * Configures a {@link Jackson2ObjectMapperBuilderCustomizer} to customize the
     * global configuration of the Jackson {@link ObjectMapper}.
     * This method registers the {@link JavaTimeModule}, which is necessary for
     * handling Java 8 date and time types, such as {@link java.time.LocalDate}.
     * Additionally, it disables the {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS}
     * feature, ensuring that dates are serialized in a human-readable format
     * (ISO-8601) instead of as timestamps.
     * @return a {@link Jackson2ObjectMapperBuilderCustomizer} configured to
     *         properly handle Java 8 date and time types.
     */
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
