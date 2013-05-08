package co.mewf.formaliser;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import co.mewf.formaliser.html.Extras;
import co.mewf.formaliser.html.Html5InputTypes;
import co.mewf.formaliser.html.TemplateRowWriter;
import co.mewf.formaliser.testutils.forms.BooleansForm;
import co.mewf.formaliser.testutils.forms.ComplexForm;
import co.mewf.formaliser.testutils.forms.ConstraintsForm;
import co.mewf.formaliser.testutils.forms.EnumForm;
import co.mewf.formaliser.testutils.forms.Html5TypesForm;
import co.mewf.formaliser.testutils.forms.NumberAttributesForm;
import co.mewf.formaliser.testutils.forms.PrimitiveNumberTypesForm;
import co.mewf.formaliser.testutils.forms.RequiredForm;
import co.mewf.formaliser.testutils.forms.SimpleForm;
import co.mewf.formaliser.testutils.forms.Utils;
import co.mewf.formaliser.testutils.forms.WrapperNumberTypesForm;
import co.mewf.formaliser.text.FormI18n;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

import org.junit.Test;

public class FormWriterClassTest {
  private final FormConfig html5Config = new FormConfig.Builder().inputTypes(new Html5InputTypes().useUrl().useDate().useColor()).build();

  @Test
  public void should_write_all_fields() throws IOException {
    FormWriter formWriter = new FormWriter(SimpleForm.class);

    String output = formWriter.writeToString();

    assertEquals(expected("should_write_all_fields"), output);
  }

  @Test
  public void should_write_named_fields() throws IOException {
    String output = new FormWriter(SimpleForm.class).writeToString("homeAddress", "workAddress");

    assertEquals(expected("should_write_named_fields"), output);
  }

  @Test(expected = IllegalArgumentException.class)
  public void should_fail_if_named_field_does_not_exist() {
    new FormWriter(SimpleForm.class).writeToString("unknownField");
  }

  @Test
  public void should_write_number_fields() {
    FormWriter formWriter = new FormWriter(PrimitiveNumberTypesForm.class);

    assertEquals(expected("should_write_number_fields"), formWriter.writeToString());

    assertEquals(expected("should_write_wrapper_number_fields"), new FormWriter(WrapperNumberTypesForm.class).writeToString());
  }

  @Test
  public void should_use_type_from_extras() {
    assertEquals(expected("should_use_type_from_extras"), new FormWriter(Html5TypesForm.class).writeToString("telephone", Extras.type("tel")));

    StringWriter writer = new StringWriter();
    MultiFormWriter formWriter = new FormWriter(ComplexForm.class, new FormConfig.Builder().writer(writer).build()).toMany("types");
    formWriter.write("telephone", Extras.type("tel"));
    assertEquals(expected("should_use_type_from_extras_for_to_many"), writer.toString());
  }

  @Test
  public void should_write_checkboxes_for_booleans() {
    assertEquals(expected("should_write_checkbox_for_booleans"), new FormWriter(BooleansForm.class).writeToString());
  }

  @Test
  public void should_write_min_and_max_for_numbers() {
    assertEquals(expected("should_write_min_and_max_for_number_fields"), new FormWriter(NumberAttributesForm.class).writeToString());
  }

  @Test
  public void should_use_maxlength_for_strings() throws Exception {
    assertEquals(expected("should_use_maxlength_for_strings"), new FormWriter(ConstraintsForm.class).writeToString("withMax"));
  }

  @Test
  public void should_write_required_attribute() {
    assertEquals(expected("should_write_required_attribute"), new FormWriter(RequiredForm.class).writeToString());
  }

  @Test
  public void should_write_email() {
    assertEquals(expected("should_write_email"), new FormWriter(Html5TypesForm.class).writeToString("email"));
  }

  @Test
  public void should_write_url_if_requested() {
    assertEquals(expected("should_write_url"), new FormWriter(Html5TypesForm.class, html5Config).writeToString("url"));
    assertEquals(expected("should_not_write_url"), new FormWriter(Html5TypesForm.class).writeToString("url"));
  }

  @Test
  public void should_write_date_if_requested() {
    assertEquals(expected("should_use_date"), new FormWriter(Html5TypesForm.class, html5Config).writeToString("date"));
    assertEquals(expected("should_not_use_date"), new FormWriter(Html5TypesForm.class).writeToString("date"));
  }

  @Test
  public void should_use_color_if_requested() {
    assertEquals(expected("should_use_color"), new FormWriter(Html5TypesForm.class, html5Config).writeToString("color"));
    assertEquals(expected("should_not_use_color"), new FormWriter(Html5TypesForm.class).writeToString("color"));
  }

  @Test
  public void should_write_select_for_enum() {
    assertEquals(expected("should_write_select_for_enum"), new FormWriter(EnumForm.class).writeToString());
  }

  @Test
  public void should_use_custom_template() {
    TemplateRowWriter rowWriter = new TemplateRowWriter("<div class=\"control-group\">{}</div></div>\n", "<label class=\"control-label\">{}</label><div class=\"controls\">");
    FormConfig config = new FormConfig.Builder().rowWriter(rowWriter).build();
    assertEquals(expected("should_use_custom_template"), new FormWriter(SimpleForm.class, config).writeToString());
  }

  @Test
  public void should_write_fields_to_specified_writer() {
    final StringWriter writer = new StringWriter();
    Writer writerWrapper = new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        writer.write(cbuf, off, len);
      }

      @Override
      public void flush() throws IOException {
        writer.flush();
      }

      @Override
      public void close() throws IOException {
        writer.close();
      }
    };

    new FormWriter(Html5TypesForm.class, new FormConfig.Builder().writer(writerWrapper).build()).write("email");

    assertEquals(expected("should_write_email"), writer.toString());
  }

  @Test
  public void should_use_i18n() {
    String html = new FormWriter(SimpleForm.class, new FormConfig.Builder().i18n(new FormI18n() {
      private int counter = 0;

      @Override
      public String getLabel(Field field) {
        return "Bonjour " + ++counter;
      }
    }).build()).writeToString();

    assertEquals(expected("should_use_i18n"), html);
  }

  @Test
  public void should_use_extra_attributes() throws Exception {
    assertEquals(expected("should_use_extra_attributes"), new FormWriter(SimpleForm.class).writeToString("name", Extras.attr("class", "my-class").a("style", "my-style").d("id", "my-id").d("location", "my-location")));
    Extras extras = Extras.attr("class", "my-class").d("id", "my-id");
    assertThat(new FormWriter(BooleansForm.class).writeToString("primitive", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
    assertThat(new FormWriter(NumberAttributesForm.class).writeToString("withMin", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
    assertThat(new FormWriter(Html5TypesForm.class).writeToString("email", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
    assertThat(new FormWriter(EnumForm.class).writeToString("accessType", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
  }

  @Test
  public void should_use_extra_attributes_for_checkboxes() throws Exception {
    Extras extras = Extras.attr("class", "my-class").d("id", "my-id");
    assertThat(new FormWriter(BooleansForm.class).writeToString("primitive", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
  }

  @Test
  public void should_use_extra_attributes_for_numbers() throws Exception {
    Extras extras = Extras.attr("class", "my-class").d("id", "my-id");
    assertThat(new FormWriter(NumberAttributesForm.class).writeToString("withMin", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
  }

  @Test
  public void should_use_extra_attributes_for_default_type() throws Exception {
    Extras extras = Extras.attr("class", "my-class").d("id", "my-id");
    assertThat(new FormWriter(Html5TypesForm.class).writeToString("email", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
  }

  @Test
  public void should_use_extra_attributes_for_select() throws Exception {
    Extras extras = Extras.attr("class", "my-class").d("id", "my-id");
    assertThat(new FormWriter(EnumForm.class).writeToString("accessType", extras), containsString("class=\"my-class\" data-id=\"my-id\""));
  }

  @Test
  public void should_write_embedded_object() throws Exception {
    FormWriter simpleFormWriter = new FormWriter(ComplexForm.class).toOne("details");
    assertEquals(expected("should_write_embedded_object"), simpleFormWriter.writeToString("name"));
  }

  private String expected(String fileName) {
    return Utils.read(getClass(), fileName + ".html");
  }
}
