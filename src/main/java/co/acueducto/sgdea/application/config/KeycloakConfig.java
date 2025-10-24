package co.acueducto.sgdea.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakConfig {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String clientUUID;

    public static KeycloakConfig from(final String configString) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            return objectMapper.readValue(configString, KeycloakConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Keycloak configuration", e);
        }

    }
}
