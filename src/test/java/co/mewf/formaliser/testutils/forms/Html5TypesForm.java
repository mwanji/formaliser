package co.mewf.formaliser.testutils.forms;

import java.awt.Color;
import java.util.Date;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.URL;

public class Html5TypesForm {

  @Email
  public String email;
  @URL
  public String url;
  public Date date;
  public Color color;
  public String telephone;
}
