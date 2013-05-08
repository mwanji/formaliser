package co.mewf.formaliser.testutils.forms;

import javax.validation.constraints.Max;

public class ConstraintsForm {

  @Max(16)
  public String withMax;
}
