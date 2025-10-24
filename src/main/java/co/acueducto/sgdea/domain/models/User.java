package co.acueducto.sgdea.domain.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private String id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String company;
}
