package co.mewf.formaliser;

import co.mewf.formaliser.html.Extras;
import co.mewf.formaliser.html.RowWriter.RowInfo;
import co.mewf.formaliser.parameters.SimpleFieldNamer;
import co.mewf.formaliser.utils.Fields;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

public class FormWriter {

  private final Class<?> formClass;
  private final Object instance;
  private final FormConfig config;

  public FormWriter(Class<?> entityClass) {
    this(entityClass, new FormConfig.Builder().build());
  }

  public FormWriter(Class<?> entityClass, FormConfig config) {
    this(entityClass, null, config);
  }

  public FormWriter(Object instance) {
    this(instance.getClass(), instance, new FormConfig.Builder().build());
  }

  public FormWriter(Object instance, FormConfig config) {
    this(instance.getClass(), instance, config);
  }

  public FormWriter toOne(String fieldName) {
    try {
      Field field = formClass.getField(fieldName);
      FormConfig subConfig = config.toBuilder().fieldNamer(config.fieldNamer.extend(field)).build();
      if (instance != null) {
        return new FormWriter(field.get(instance), subConfig);
      } else {
        return new FormWriter(field.getType(), subConfig);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public MultiFormWriter toMany(String fieldName) {
    return new MultiFormWriter(instance != null ? instance : formClass, fieldName, config);
  }

  /**
   * Writes all fields.
   */
  public void write() {
    write(config.writer, Extras.NONE, formClass.getFields());
  }

  /**
   * Writes the given fields.
   * @param fieldNames
   */
  public void write(String... fieldNames) {
    write(config.writer, Extras.NONE, toFields(fieldNames));
  }

  public void write(String fieldName, Extras extras) {
    write(config.writer, extras, toFields(fieldName));
  }

  public String writeToString() {
    StringWriter stringWriter = new StringWriter();
    write(stringWriter, Extras.NONE, formClass.getFields());

    return stringWriter.toString();
  }

  public String writeToString(String... fieldNames) {
    StringWriter stringWriter = new StringWriter();
    write(stringWriter, Extras.NONE, toFields(fieldNames));

    return stringWriter.toString();
  }

  public String writeToString(String fieldName, Extras extras) {
    StringWriter stringWriter = new StringWriter();
    write(stringWriter, extras, toFields(fieldName));

    return stringWriter.toString();
  }

  public Object getInstance() {
    return instance;
  }

  private void write(Writer writer, Extras extras, Field... fields) {
    for (Field field : fields) {
      try {
        writeField(field, "", extras, writer);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void writeField(Field field, String root, Extras extras, Writer writer) throws Exception {
    field.setAccessible(true);
    String label = config.i18n.getLabel(field);

    String type = extras.hasType() ? extras.getType() : config.inputTypes.getInputType(field);
    String name = config.fieldNamer.getName(field);
    String value = null;
    if (instance != null) {
      Object rawValue = field.get(instance);
      if (rawValue != null) {
        value = rawValue.toString();
      }
    }

    config.rowWriter.write(new RowInfo(type, label, name, value, extras), field, writer);
  }

  private Field[] toFields(String... fieldNames) {
    return Fields.toFields(formClass, fieldNames);
  }

  private FormWriter(Class<?> entityClass, Object instance, FormConfig config) {
    this.formClass = entityClass;
    this.instance = instance;
    this.config = config.fieldNamer != null ? config : config.toBuilder().fieldNamer(new SimpleFieldNamer(entityClass)).build();
  }
}
