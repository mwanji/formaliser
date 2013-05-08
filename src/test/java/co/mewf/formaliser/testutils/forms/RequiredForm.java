package co.mewf.formaliser.testutils.forms;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public class RequiredForm {

  @NotNull
  public Long requiredLong;
  @NotEmpty
  public String requiredString;
  @Column(nullable = false)
  public String notNullableString;
}
