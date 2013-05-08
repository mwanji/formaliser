package co.mewf.formaliser.html;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Date;

import org.apache.commons.lang3.ClassUtils;

/**
 * Uses HTML5 input types. By default, it only uses the ones that are safe: number and email.
 *
 * To enable types that can sometimes be problematic, call the useXXX() methods, e.g. new Html5InputTypes().useUrl()
 */
public class Html5InputTypes implements InputTypes {
  private boolean useUrl = false;
  private boolean useDate = false;
  private boolean useColor = false;

  @Override
  public String getInputType(Member member) {
    Class<?> type = ((Field) member).getType();
    if (type == boolean.class || type == Boolean.class) {
      return "checkbox";
    }

    if (ClassUtils.isPrimitiveOrWrapper(type)) {
      return "number";
    }

    if (Enum.class.isAssignableFrom(type)) {
      return "select";
    }

    if (useDate && Date.class.isAssignableFrom(type)) {
      return "date";
    }

    if (useColor && Color.class.isAssignableFrom(type)) {
      return "color";
    }

    for (Annotation annotation : ((AccessibleObject) member).getAnnotations()) {
      if (annotation.annotationType().getName().equals("org.hibernate.validator.constraints.Email")) {
        return "email";
      }

      if (useUrl && annotation.annotationType().getName().equals("org.hibernate.validator.constraints.URL")) {
        return "url";
      }
    }

    return "text";
  }

  public Html5InputTypes useUrl() {
    this.useUrl = true;
    return this;
  }

  public Html5InputTypes useDate() {
    this.useDate  = true;
    return this;
  }

  public Html5InputTypes useColor() {
    this.useColor = true;
    return this;
  }
}
