package co.mewf.formaliser;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import co.mewf.formaliser.html.Extras;
import co.mewf.formaliser.html.FormWriterBlock;
import co.mewf.formaliser.html.Html5InputTypes;
import co.mewf.formaliser.testutils.forms.ComplexForm;
import co.mewf.formaliser.testutils.forms.EnumForm;
import co.mewf.formaliser.testutils.forms.Html5TypesForm;
import co.mewf.formaliser.testutils.forms.MultiObjectsForm;
import co.mewf.formaliser.testutils.forms.SimpleForm;
import co.mewf.formaliser.testutils.forms.Utils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.persistence.AccessType;

import org.junit.Test;

public class FormWriterInstanceTest {

  @Test
  public void should_write_all_fields() {
    SimpleForm form = new SimpleForm();
    form.name = "User";
    form.homeAddress = "Home";
    form.workAddress = "Work";

    FormWriter formWriter = new FormWriter(form);

    assertEquals(expected("should_write_all_fields"), formWriter.writeToString());
  }

  @Test
  public void should_not_set_value_on_null_fields() {
    SimpleForm form = new SimpleForm();
    form.name = "User";
    form.homeAddress = "Home";

    FormWriter formWriter = new FormWriter(form);

    assertEquals(expected("should_not_set_value_on_null_fields"), formWriter.writeToString());
  }

  @Test
  public void should_set_value_for_select_for_enum() {
    EnumForm enumForm = new EnumForm();
    enumForm.accessType = AccessType.FIELD;
    enumForm.requiredAccessType = AccessType.PROPERTY;

    assertEquals(expected("should_set_value_for_select_for_enum"), new FormWriter(enumForm).writeToString());
  }

  @Test
  public void should_write_ordered_fields_for_list() throws Exception {
    MultiObjectsForm form = new MultiObjectsForm();
    SimpleForm form0 = new SimpleForm();
    form0.name = "User 0";
    form0.homeAddress = "Home 0";
    form0.workAddress = "Work 0";
    SimpleForm form1 = new SimpleForm();
    form1.name = "User 1";
    form1.homeAddress = "Home 1";
    form1.workAddress = "Work 1";
    form.simples = asList(form0, form1);

    StringWriter writer = new StringWriter();
    FormWriter formWriter = new FormWriter(form, new FormConfig.Builder().writer(writer).build());
    MultiFormWriter simplesFormWriter = formWriter.toMany("simples");
    simplesFormWriter.write(0, "name", "homeAddress", "workAddress");
    writer.append("\n");
    simplesFormWriter.write(1, "name", "homeAddress", "workAddress");

    assertEquals(expected("should_write_ordered_embedded_objects"), writer.toString());
  }

  @Test
  public void should_use_extra_attributes() throws Exception {
    SimpleForm form0 = new SimpleForm();
    form0.name = "My Name";
    MultiObjectsForm form = new MultiObjectsForm();
    form.simples = asList(form0);

    StringWriter writer = new StringWriter();
    FormConfig config = new FormConfig.Builder().writer(writer).build();
    MultiFormWriter formWriter = new FormWriter(form, config).toMany("simples");
    formWriter.write(0, "name", Extras.data("location", "my-location").a("class", "my-class"));

    assertEquals(expected("should_use_extra_attributes"), writer.toString());
 }

  @Test
  public void should_write_embedded_object() throws Exception {
    ComplexForm complexForm = new ComplexForm();
    SimpleForm simpleForm = new SimpleForm();
    simpleForm.name = "My Name";
    complexForm.details = simpleForm;

    FormWriter simpleFormWriter = new FormWriter(complexForm).toOne("details");
    assertEquals(expected("should_write_embedded_object"), simpleFormWriter.writeToString("name"));
  }

  @Test
  public void should_write_all_collection_elements() throws Exception {
    Html5TypesForm types1 = new Html5TypesForm();
    types1.email = "me@email1.com";
    types1.url = "http://email1.com";
    Html5TypesForm types2 = new Html5TypesForm();
    types2.email = "me@email2.com";
    types2.url = "http://email2.com";
    ComplexForm complexForm = new ComplexForm();
    complexForm.types = new ArrayList<Html5TypesForm>();
    complexForm.types.add(types1);
    complexForm.types.add(types2);

    StringWriter writer = new StringWriter();
    FormConfig config = new FormConfig.Builder().inputTypes(new Html5InputTypes().useUrl()).writer(writer).build();
    MultiFormWriter formWriter = new FormWriter(complexForm, config).toMany("types");

    formWriter.forEach(new FormWriterBlock<Html5TypesForm>() {
      @Override
      public void write(FormWriter itemFormWriter, Html5TypesForm item, Writer writer) throws Exception {
        writer.write("<fieldset>\n<legend>");
        writer.write(item.email);
        writer.write("</legend>\n");
        itemFormWriter.write("email", "url");
        writer.write("</fieldset>\n");
      }
    });

    assertEquals(expected("should_write_all_collection_elements"), writer.toString());
  }

  private String expected(String fileName) {
    return Utils.read(getClass(), fileName + ".html");
  }
}
