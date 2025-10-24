package co.acueducto.sgdea.domain.port.out;

import co.acueducto.sgdea.application.config.KeycloakConfig;
import co.acueducto.sgdea.domain.models.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public interface IdentityProviderUserPort {

  List<User> getUsersByRolAndClientUUID(GetUsersByRolAndClientUUIDCommand command);

  @Getter
  @Builder
  class GetUsersByRolAndClientUUIDCommand {
    private String roleName;
    private KeycloakConfig keycloakConfig;
  }
}
