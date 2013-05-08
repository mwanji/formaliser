package co.mewf.formaliser.testutils.forms;

import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class NumberAttributesForm {

  @Min(0)
  public int withMin;
  @Max(5)
  public long withMax;
  @Min(3) @Max(7)
  public Short withMinAndMax;
}
