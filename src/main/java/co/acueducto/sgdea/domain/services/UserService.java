package co.acueducto.sgdea.domain.services;

import co.acueducto.sgdea.domain.models.User;
import co.acueducto.sgdea.domain.port.in.GetUsersUseCase;
import co.acueducto.sgdea.domain.port.out.IdentityProviderUserPort;
import co.acueducto.sgdea.domain.port.out.IdentityProviderUserPort.GetUsersByRolAndClientUUIDCommand;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService implements GetUsersUseCase {

  private final IdentityProviderUserPort identityProviderUserPort;

  @Override
  public List<User> getUsersByRolAndDependency(final GetUsersByRolAndDependencyCommand command) {
    final GetUsersByRolAndClientUUIDCommand commandGetUsers =
        GetUsersByRolAndClientUUIDCommand.builder()
            .roleName(command.getRoleName())
            .keycloakConfig(command.getKeycloakConfig())
            .build();

    final List<User> userList =
        identityProviderUserPort.getUsersByRolAndClientUUID(commandGetUsers);
    return userList.stream()
        .filter(user -> user.getCompany().contains(command.getDependency()))
        .collect(Collectors.toList());
  }
}
