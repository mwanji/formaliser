package co.mewf.formaliser.testutils.forms;

import javax.persistence.AccessType;
import javax.validation.constraints.NotNull;

public class EnumForm {

  public AccessType accessType;
  
  @NotNull
  public AccessType requiredAccessType;
}
