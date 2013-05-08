package co.mewf.formaliser.text;


import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

import java.lang.reflect.Field;

public class FieldNameFormI18n implements FormI18n {

  /**
   * Creates a label by humanising a camelcased field name.
   */
  @Override
  public String getLabel(Field field) {
    return capitalize(join(splitByCharacterTypeCamelCase(field.getName()), ' '));
  }
}
