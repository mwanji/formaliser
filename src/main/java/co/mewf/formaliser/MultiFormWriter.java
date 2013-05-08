package co.mewf.formaliser;

import static co.mewf.formaliser.utils.Fields.toFields;
import co.mewf.formaliser.html.Extras;
import co.mewf.formaliser.html.FormWriterBlock;
import co.mewf.formaliser.html.RowWriter.RowInfo;
import co.mewf.formaliser.parameters.FieldNamer;
import co.mewf.formaliser.utils.Fields;

import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MultiFormWriter {

  private static final int NO_ITEM = -1;
  private final Class<?> collectionClass;
  private final Class<?> componentClass;
  private final FieldNamer fieldNamer;
  private final FormConfig config;
  private final Object instance;
  private Field instanceField;

  MultiFormWriter(Object instance, String fieldName, FormConfig config) {
    this.config = config;
    try {
      Class<? extends Object> containerClass = instance instanceof Class ? (Class<?>) instance : instance.getClass();
      this.instanceField = containerClass.getField(fieldName);
      this.instance = instance instanceof Class ? null : instanceField.get(instance);
      this.collectionClass = instanceField.getType();
      this.componentClass = Fields.getComponentClass(instanceField);
      this.fieldNamer = config.fieldNamer.extend(instanceField);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Constructs a field for an empty collection. Creates an instance of the component class using the default constructor.
   */
  public void write(String fieldName, Extras extras) {
    write(NO_ITEM, extras, toFields(componentClass, fieldName));
  }


  public <T> void forEach(FormWriterBlock<T> formWriterBlock) {
    Iterator<?> it = ((Iterable<?>) instance).iterator();

    for (int i = 0; it.hasNext(); i++) {
      Object item = it.next();
      try {
        formWriterBlock.write(new FormWriter(item, config.toBuilder().fieldNamer(fieldNamer.extend(i)).build()), (T) item, config.writer);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void write(int index, String fieldName, Extras extras) {
    write(index, extras, toFields(componentClass, fieldName));
  }

  public void write(int index, String... fieldNames) {
    write(index, Extras.NONE, toFields(componentClass, fieldNames));
  }

  private void write(int index, Extras extras, Field... fields) {
    for (Field field : fields) {
      writeField(field, get(index), index, extras, config.writer);
    }
  }

  private void writeField(Field field, Object item, int index, Extras extras, Writer writer) {
    try {
      field.setAccessible(true);
      String label = config.i18n.getLabel(field);

      String type = extras.hasType() ? extras.getType() : config.inputTypes.getInputType(field);
      String name =fieldNamer.getName(field, Math.max(index, 0));
      String value = null;
      if (item != null) {
        Object rawValue = field.get(item);
        if (rawValue != null) {
          value = rawValue.toString();
        }
      }

      config.rowWriter.write(new RowInfo(type, label, name, value, extras), field, writer);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Object get(int index) {
    if (index == NO_ITEM) {
      try {
        return componentClass.getConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    if (collectionClass.isArray()) {
      return Array.get(instance, index);
    }

    if (instance instanceof List) {
      return ((List<?>) instance).get(index);
    }

    Iterator<?> it = ((Collection<?>) instance).iterator();

    for (int i = 0; i < index; i++) {
      it.next();
    }

    return it.next();
  }
}
