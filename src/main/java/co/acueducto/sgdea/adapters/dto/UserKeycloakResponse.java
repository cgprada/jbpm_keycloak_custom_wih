package co.acueducto.sgdea.adapters.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKeycloakResponse implements Serializable {
  private static final long serialVersionUID = 1L;

  private String id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private boolean emailVerified;
  private Map<String, List<String>> attributes;
  private Long createdTimestamp;
  private boolean enabled;
  private boolean totp;
  private String federationLink;
  private List<String> disableableCredentialTypes;
  private List<String> requiredActions;
  private int notBefore;
}
