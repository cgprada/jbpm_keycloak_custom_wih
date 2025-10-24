package co.acueducto.sgdea;

import co.acueducto.sgdea.adapters.IdentityProviderUserAdapter;
import co.acueducto.sgdea.application.config.KeycloakConfig;
import co.acueducto.sgdea.domain.models.User;
import co.acueducto.sgdea.domain.port.in.GetUsersUseCase;
import co.acueducto.sgdea.domain.port.out.IdentityProviderUserPort;
import co.acueducto.sgdea.domain.services.UserService;
import java.util.List;

public class KeycloakWIHApplication {

  public static void main(String[] args) {
    // config keycloak
    final String configString =
        "{ \"serverUrl\": \"https://sgdea-acueducto.linktic.com\", \"realm\": \"LinkTic\", \"clientId\": \"business-central-dev\", \"clientSecret\": \"Me9aHacgs4Z4VuhlxNIEJqVLWKnhBhP0\", \"clientUUID\": \"bc4a10bb-c834-4198-8c4d-56eda89e9117\" }";
    final KeycloakConfig keycloakConfig = KeycloakConfig.from(configString);

    IdentityProviderUserPort identityProviderUserPort = new IdentityProviderUserAdapter();
    GetUsersUseCase getUsersUseCase = new UserService(identityProviderUserPort);
    final GetUsersUseCase.GetUsersByRolAndDependencyCommand command =
        GetUsersUseCase.GetUsersByRolAndDependencyCommand.builder()
            .roleName("GRP_SGDEA_Tipificador")
            .dependency("3310001")
            .keycloakConfig(keycloakConfig)
            .build();
    final List<User> user = getUsersUseCase.getUsersByRolAndDependency(command);
    System.out.println(user.toString());
  }
}
