package co.acueducto.sgdea.adapters;

import co.acueducto.sgdea.adapters.dto.UserKeycloakResponse;
import co.acueducto.sgdea.application.config.KeycloakConfig;
import co.acueducto.sgdea.domain.models.User;
import co.acueducto.sgdea.domain.port.out.IdentityProviderUserPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IdentityProviderUserAdapter implements IdentityProviderUserPort {
  private static final String GET_USERS_BY_ROL_CLIENT_PATH =
      "/admin/realms/%s/clients/%s/roles/%s/users";
  private static final String GENERATE_TOKEN_PATH = "/realms/%s/protocol/openid-connect/token";

  @Override
  public List<User> getUsersByRolAndClientUUID(GetUsersByRolAndClientUUIDCommand command) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      final KeycloakConfig keycloakConfig = command.getKeycloakConfig();
      final URL url =
          new URL(
              keycloakConfig.getServerUrl()
                  + String.format(
                      GET_USERS_BY_ROL_CLIENT_PATH,
                      keycloakConfig.getRealm(),
                      keycloakConfig.getClientUUID(),
                      command.getRoleName()));
      final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty(
          "Authorization",
          "Bearer "
              + generateToken(
                  keycloakConfig.getClientId(),
                  keycloakConfig.getClientSecret(),
                  keycloakConfig.getServerUrl(),
                  keycloakConfig.getRealm()));
      try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          response.append(line);
        }
        List<UserKeycloakResponse> responseDto =
            objectMapper.readValue(
                response.toString(),
                objectMapper
                    .getTypeFactory()
                    .constructCollectionType(List.class, UserKeycloakResponse.class));
        return responseDtotoUserList(responseDto);
      } finally {
        conn.disconnect();
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String generateToken(
      final String clientId,
      final String clientSecret,
      final String keycloakUrl,
      final String realm) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      StringBuilder formData =
          new StringBuilder()
              .append("client_id=")
              .append(URLEncoder.encode(clientId, "UTF-8"))
              .append("&client_secret=")
              .append(URLEncoder.encode(clientSecret, "UTF-8"))
              .append("&grant_type=")
              .append(URLEncoder.encode("client_credentials", "UTF-8"));

      HttpURLConnection conn = getHttpURLConnection(keycloakUrl, realm, formData);

      try (BufferedReader br =
          new BufferedReader(
              new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
          response.append(responseLine.trim());
        }
        return objectMapper.readTree(response.toString()).get("access_token").asText();
      } finally {
        conn.disconnect();
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate token", e);
    }
  }

  private static HttpURLConnection getHttpURLConnection(
      String keycloakUrl, String realm, StringBuilder formData) throws IOException {
    URL url = new URL(keycloakUrl + String.format(GENERATE_TOKEN_PATH, realm));
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      byte[] input = formData.toString().getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }
    return conn;
  }

  private List<User> responseDtotoUserList(List<UserKeycloakResponse> response) {
    ObjectMapper objectMapper = new ObjectMapper();
    return response.stream()
        .map(
            userKeycloakResponse -> {
              return User.builder()
                  .id(userKeycloakResponse.getId())
                  .email(userKeycloakResponse.getEmail())
                  .firstName(userKeycloakResponse.getFirstName())
                  .lastName(userKeycloakResponse.getLastName())
                  .username(userKeycloakResponse.getUsername())
                  .company(getAttributeCompany(userKeycloakResponse))
                  .build();
            })
        .collect(Collectors.toList());
  }

  private String getAttributeCompany(UserKeycloakResponse user) {
    if (user.getAttributes() != null && user.getAttributes().containsKey("company")) {
      List<String> companies = user.getAttributes().get("company");
      if (companies != null && !companies.isEmpty()) {
        return companies.get(0);
      }
    }
    return "";
  }
}
