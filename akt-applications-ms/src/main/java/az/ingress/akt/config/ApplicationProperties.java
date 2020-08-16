package az.ingress.akt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
@Getter
public class ApplicationProperties {

    private final Security security = new Security();

    private final CorsConfiguration cors = new CorsConfiguration();


    @Getter
    @Setter
    public static class Security {

        private String secret;

    }

}
