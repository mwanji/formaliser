package co.mewf.formaliser.html;


import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.text.WordUtils;

/**
 * <p>Uses two templates to control output: one for the row and one for the label.
 * In both cases, <code>{}</code> is used as a placeholder.</p>
 *
 * The no-args constructor uses <code>"&lt;div&gt;{}&lt;/div&gt;\n"</code> and <code>"&lt;label&gt;{}&lt;/label&gt;"</code>.
 */
public class TemplateRowWriter implements RowWriter {

  private final String rowOpenTemplate;
  private final String rowCloseTemplate;
  private final String labelOpenTemplate;
  private final String labelCloseTemplate;

  public TemplateRowWriter() {
    this("<div>{}</div>\n", "<label>{}</label>");
  }

  public TemplateRowWriter(String rowTemplate, String labelTemplate) {
    this.rowOpenTemplate = rowTemplate.substring(0, rowTemplate.indexOf('{'));
    this.rowCloseTemplate = rowTemplate.substring(rowTemplate.indexOf('}') + 1);
    this.labelOpenTemplate = labelTemplate.substring(0, labelTemplate.indexOf('{'));
    this.labelCloseTemplate = labelTemplate.substring(labelTemplate.indexOf('}') + 1);
  }

  @Override
  public void write(RowInfo rowInfo, Field field, Writer writer) throws IOException {
    openRow(writer);
    if ("checkbox".equals(rowInfo.type)) {
      checkbox(rowInfo, field, writer);
    } else if ("select".equals(rowInfo.type)) {
      select(rowInfo, field, writer);
    } else if ("number".equals(rowInfo.type)) {
      number(rowInfo, field, writer);
    } else if ("text".equals(rowInfo.type)) {
      text(rowInfo, field, writer);
    } else {
      input(rowInfo, rowInfo.value, rowInfo.extras, field, writer);
    }
    closeRow(writer);
  }

  private void text(RowInfo rowInfo, Field field, Writer writer) throws IOException {
    Extras extras = rowInfo.extras.copy();

    if (field.isAnnotationPresent(Max.class)) {
      extras.a("maxlength", Long.toString(field.getAnnotation(Max.class).value()));
    }

    input(rowInfo, rowInfo.value, extras, field, writer);
  }

  private void number(RowInfo rowInfo, Field field, Writer writer) throws IOException {
    Extras extras = rowInfo.extras.copy();
    if (field.isAnnotationPresent(Min.class)) {
      extras.a("min", Long.toString(field.getAnnotation(Min.class).value()));
    }

    if (field.isAnnotationPresent(Max.class)) {
      extras.a("max", Long.toString(field.getAnnotation(Max.class).value()));
    }

    input(rowInfo, rowInfo.value, extras, field, writer);
  }

  private void select(RowInfo rowInfo, Field field, Writer writer) throws IOException {
    label(rowInfo.label, writer);
    writer.write("<select name=\"");
    writer.write(rowInfo.name);
    writer.write("\"");
    requiredAttribute(field, writer);
    writer.write(rowInfo.extras.toString());
    writer.write(">");
    try {
      Object[] options = (Object[]) field.getType().getMethod("values").invoke(null);
      for (Object option : options) {
        String optionAsString = option.toString();
        writer.write("<option");
        valueAttribute(optionAsString, writer);
        if (optionAsString.equals(rowInfo.value)) {
          writer.write(" selected");
        }
        writer.write(">");
        writer.write(WordUtils.capitalizeFully(optionAsString));
        writer.write("</option>");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    writer.write("</select>");
  }

  private void checkbox(RowInfo rowInfo, Field field, Writer writer) throws IOException {
    openInput(rowInfo.name, "hidden", writer);
    valueAttribute("false", writer);
    closeInput(writer);
    writer.write("\n");
    input(rowInfo, "true", rowInfo.extras, field, writer);
  }

  private void input(RowInfo rowInfo, String value, Extras extras, Field field, Writer writer) throws IOException {
    label(rowInfo.label, writer);
    openInput(rowInfo.name, rowInfo.type, writer);
    requiredAttribute(field, writer);
    valueAttribute(value, writer);
    writer.write(extras.toString());
    closeInput(writer);
  }

  private void openRow(Writer writer) throws IOException {
    writer.write(rowOpenTemplate);
  }

  private void closeRow(Writer writer) throws IOException {
    writer.write(rowCloseTemplate);
  }

  private void label(String label, Writer writer) throws IOException {
    writer.write(labelOpenTemplate);
    writer.write(label);
    writer.write(labelCloseTemplate);
  }

  private void openInput(String name, String type, Writer writer) throws IOException {
    writer.write("<input type=\"" + type + "\" name=\"");
    writer.write(name);
    writer.write("\"");
  }

  private void closeInput(Writer writer) throws IOException {
    writer.write(" />");
  }

  private void requiredAttribute(Field field, Writer writer) throws IOException {
    for (Annotation annotation : field.getAnnotations()) {
      if (annotation instanceof NotNull || (annotation instanceof Column && !((Column) annotation).nullable()) || annotation.annotationType().getName().equals("org.hibernate.validator.constraints.NotEmpty")) {
        attribute("required", "required", writer);
      }
    }
  }

  private void valueAttribute(String value, Writer writer) throws IOException {
    if (value != null) {
      attribute("value", value, writer);
    }
  }

  private void attribute(String name, String value, Writer writer) throws IOException {
    writer.write(" ");
    writer.write(name);
    writer.write("=\"");
    writer.write(value);
    writer.write("\"");
  }
}
