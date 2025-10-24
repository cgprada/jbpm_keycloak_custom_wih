package co.acueducto.sgdea.application;

import co.acueducto.sgdea.application.config.KeycloakConfig;
import co.acueducto.sgdea.domain.models.User;
import co.acueducto.sgdea.domain.port.in.GetUsersUseCase;
import co.acueducto.sgdea.domain.port.in.GetUsersUseCase.GetUsersByRolAndDependencyCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

@RequiredArgsConstructor
public class UserApplication implements WorkItemHandler {

  private static final String PARAM_INPUT_ROLE_NAME = "roleName";
  private static final String PARAM_INPUT_DEPENDENCY = "dependency";
  private static final String PARAM_INPUT_CONFIG_KEYCLOAK = "configKeycloak";

  private static final String PARAM_OUTPUT_USER_LIST = "userList";

  private final GetUsersUseCase getUsersUseCase;

  @Override
  public void executeWorkItem(final WorkItem workItem, final WorkItemManager workItemManager) {
    final String roleName = (String) workItem.getParameter(PARAM_INPUT_ROLE_NAME);
    final String dependency = (String) workItem.getParameter(PARAM_INPUT_DEPENDENCY);
    final String configKeycloakStr = (String) workItem.getParameter(PARAM_INPUT_CONFIG_KEYCLOAK);

    final KeycloakConfig keycloakConfig = KeycloakConfig.from(configKeycloakStr);
    final GetUsersByRolAndDependencyCommand command =
        GetUsersByRolAndDependencyCommand.builder()
            .roleName(roleName)
            .dependency(dependency)
            .keycloakConfig(keycloakConfig)
            .build();
    final List<String> userNameList =
        getUsersUseCase.getUsersByRolAndDependency(command).stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
     final String userListStr = String.join(", ", userNameList);

    final Map<String, Object> results = new HashMap<>();
    results.put(PARAM_OUTPUT_USER_LIST, userListStr);
    workItemManager.completeWorkItem(workItem.getId(), results);
  }

  @Override
  public void abortWorkItem(WorkItem workItem, WorkItemManager workItemManager) {}
}
