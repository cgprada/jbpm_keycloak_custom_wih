package co.acueducto.sgdea.domain.port.in;

import co.acueducto.sgdea.application.config.KeycloakConfig;
import co.acueducto.sgdea.domain.models.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public interface GetUsersUseCase {

  List<User> getUsersByRolAndDependency(GetUsersByRolAndDependencyCommand command);

  @Getter
  @Builder
  class GetUsersByRolAndDependencyCommand {
    private String roleName;
    private String dependency;
    private KeycloakConfig keycloakConfig;
  }
}
