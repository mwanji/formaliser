package co.mewf.formaliser;

import co.mewf.formaliser.html.Html5InputTypes;
import co.mewf.formaliser.html.InputTypes;
import co.mewf.formaliser.html.RowWriter;
import co.mewf.formaliser.html.TemplateRowWriter;
import co.mewf.formaliser.parameters.FieldNamer;
import co.mewf.formaliser.text.FieldNameFormI18n;
import co.mewf.formaliser.text.FormI18n;

import java.io.Writer;

public class FormConfig {

  public static class Builder {
    private InputTypes inputTypes = new Html5InputTypes();
    private RowWriter rowWriter = new TemplateRowWriter();
    private Writer writer = null;
    private FormI18n i18n = new FieldNameFormI18n();
    private FieldNamer fieldNamer = null;

    public FormConfig.Builder inputTypes(InputTypes inputTypes) {
      this.inputTypes = inputTypes;
      return this;
    }

    public FormConfig.Builder rowWriter(RowWriter rowWriter) {
      this.rowWriter = rowWriter;
      return this;
    }

    public FormConfig.Builder writer(Writer writer) {
      this.writer = writer;
      return this;
    }

    public FormConfig.Builder i18n(FormI18n i18n) {
      this.i18n = i18n;
      return this;
    }

    public FormConfig.Builder fieldNamer(FieldNamer fieldNamer) {
      this.fieldNamer = fieldNamer;
      return this;
    }

    public FormConfig build() {
      return new FormConfig(inputTypes, rowWriter, writer, i18n, fieldNamer);
    }
  }

  public final InputTypes inputTypes;
  public final RowWriter rowWriter;
  public final Writer writer;
  public final FormI18n i18n;
  public final FieldNamer fieldNamer;

  public FormConfig(InputTypes inputTypes, RowWriter rowWriter, Writer writer, FormI18n i18n, FieldNamer fieldNamer) {
    this.inputTypes = inputTypes;
    this.rowWriter = rowWriter;
    this.writer = writer;
    this.i18n = i18n;
    this.fieldNamer = fieldNamer;
  }

  public FormConfig.Builder toBuilder() {
    return new FormConfig.Builder().fieldNamer(fieldNamer).i18n(i18n).inputTypes(inputTypes).rowWriter(rowWriter).writer(writer);
  }
}