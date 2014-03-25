package co.mewf.formaliser;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import co.mewf.formaliser.testutils.forms.BooleansForm;
import co.mewf.formaliser.testutils.forms.ComplexForm;
import co.mewf.formaliser.testutils.forms.MultiValuedForm;
import co.mewf.formaliser.testutils.forms.PrimitiveNumberTypesForm;
import co.mewf.formaliser.testutils.forms.SimpleForm;

public class FormReaderTest {

  private Map<String, List<String>> parameters = new HashMap<String, List<String>>();

  @Test
  public void should_ignore_missing_fields() {
    FormReader<SimpleForm> formReader = new FormReader<SimpleForm>(SimpleForm.class);

    SimpleForm simpleForm = formReader.read(parameters);

    assertNull(simpleForm.name);
    assertNull(simpleForm.homeAddress);
    assertNull(simpleForm.workAddress);
  }

  @Test
  public void should_ignore_missing_multi_valued_fields() throws Exception {
    FormReader<MultiValuedForm> formReader = new FormReader<MultiValuedForm>(MultiValuedForm.class);

    params("multiValuedForm", "a", "b");
    MultiValuedForm form = formReader.read(parameters);

    assertNull(form.stringArray);
    assertNull(form.stringList);
    assertNull(form.stringSet);
  }

  @Test
  public void should_read_string_fields() {
    FormReader<SimpleForm> formReader = new FormReader<SimpleForm>(SimpleForm.class);

    params("simpleForm", "name", "my name", "homeAddress", "my home address", "workAddress", "my work address");

    SimpleForm simpleForm = formReader.read(parameters);

    assertEquals("my name", simpleForm.name);
    assertEquals("my home address", simpleForm.homeAddress);
    assertEquals("my work address", simpleForm.workAddress);
  }

  @Test
  public void should_read_boolean_fields() {
    FormReader<BooleansForm> formReader = new FormReader<BooleansForm>(BooleansForm.class);

    params("booleansForm", "primitive", "true", "wrapper", "false");

    BooleansForm booleansForm = formReader.read(parameters);

    assertTrue(booleansForm.primitive);
    assertFalse(booleansForm.wrapper);
  }

  @Test
  public void should_read_primitive_fields() {
    FormReader<PrimitiveNumberTypesForm> formReader = new FormReader<PrimitiveNumberTypesForm>(PrimitiveNumberTypesForm.class);

    params("primitiveNumberTypesForm", "byteField", "1", "shortField", "10", "integer", "100", "longField", "1000", "floatField", "10000.0", "doubleField", "100000.0");

    PrimitiveNumberTypesForm primitiveNumberTypesForm = formReader.read(parameters);

    assertEquals(1, primitiveNumberTypesForm.byteField);
    assertEquals(10, primitiveNumberTypesForm.shortField);
    assertEquals(100, primitiveNumberTypesForm.integer);
    assertEquals(1000, primitiveNumberTypesForm.longField);
    assertEquals(10000.0, primitiveNumberTypesForm.floatField, 0);
    assertEquals(100000.0, primitiveNumberTypesForm.doubleField, 0);
  }

  @Test
  public void should_read_unordered_multi_valued_fields() throws Exception {
    FormReader<MultiValuedForm> formReader = new FormReader<MultiValuedForm>(MultiValuedForm.class);

    parameters.put("multiValuedForm.stringArray", asList("a", "b", "c"));
    parameters.put("multiValuedForm.stringList", asList("d", "e", "f"));
    parameters.put("multiValuedForm.stringSet", asList("g", "h", "i"));

    MultiValuedForm form = formReader.read(parameters);
    ArrayList<String> stringList = new ArrayList<String>();
    stringList.addAll(asList("d", "e", "f"));
    HashSet<String> stringSet = new HashSet<String>();
    stringSet.addAll(asList("g", "h", "i"));

    assertArrayEquals(new String[] { "a", "b", "c" }, form.stringArray);
    assertEquals(stringList, form.stringList);
    assertEquals(stringSet, form.stringSet);
  }

  @Test
  public void should_read_ordered_multi_valued_fields() throws Exception {
    FormReader<MultiValuedForm> formReader = new FormReader<MultiValuedForm>(MultiValuedForm.class);

    params("multiValuedForm", "stringArray[2]", "c", "stringArray[0]", "a", "stringArray[1]", "b");
    params("multiValuedForm", "stringList[1]", "e", "stringList[2]", "f", "stringList[0]", "d");

    MultiValuedForm form = formReader.read(parameters);
    ArrayList<String> stringList = new ArrayList<String>();
    stringList.addAll(asList("d", "e", "f"));

    assertArrayEquals(new String[] { "a", "b", "c" }, form.stringArray);
    assertEquals(stringList, form.stringList);
    assertNull(form.stringSet);
  }

  @Test
  public void should_read_embedded_object() throws Exception {
    FormReader<ComplexForm> formReader = new FormReader<ComplexForm>(ComplexForm.class);

    params("complexForm", "details.name", "My Name", "details.homeAddress", "My Home", "details.workAddress", "My Work", "name", "Form Name");

    ComplexForm form = formReader.read(parameters);

    assertEquals("Form Name", form.name);
    assertEquals("My Name", form.details.name);
    assertEquals("My Home", form.details.homeAddress);
    assertEquals("My Work", form.details.workAddress);
  }

  private void params(String root, String... params) {
    for (int i = 0; i < params.length; i = i + 2) {
      parameters.put(root + "." + params[i], Arrays.asList(params[i + 1]));
    }
  }
}
