package co.mewf.formaliser;

import co.mewf.formaliser.parameters.DefaultParameterReader;
import co.mewf.formaliser.parameters.FieldNamer;
import co.mewf.formaliser.parameters.SimpleFieldNamer;
import co.mewf.formaliser.utils.Fields;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class FormReader<T> {

  private final FieldNamer fieldNamer;
  private final DefaultParameterReader fieldHandler = new DefaultParameterReader();
  private final Class<T> rootClass;
  private final String parentPath;
  private final String alias;

  public FormReader(Class<T> rootClass) {
    this.rootClass = rootClass;
    this.fieldNamer = new SimpleFieldNamer(rootClass);
    this.alias = fieldNamer.getRoot();
    this.parentPath = alias;
  }

  private FormReader(Class<T> currentClass, String alias, String parentPath) {
    this.alias = alias;
    this.parentPath = parentPath;
    this.rootClass = currentClass;
    this.fieldNamer = new SimpleFieldNamer(parentPath);
  }

  public T read(Map<String, List<String>> parameters) {
    try {
      T instance = rootClass.getConstructor().newInstance();

      for (Field field : rootClass.getDeclaredFields()) {
        field.setAccessible(true);

        String parameterName = fieldNamer.getName(field);

        Class<?> fieldType = field.getType();

        Object value = null;

        if (fieldHandler.handles(field)) {
          value = fieldHandler.handle(field, parameterName, parameters);
        } else if (!Fields.isCollection(field) && !Fields.isArray(field)) {
          @SuppressWarnings({ "unchecked", "rawtypes" })
          FormReader<?> formReader = new FormReader(fieldType, field.getName(), parameterName);
          value = formReader.read(parameters);
        }

        field.set(instance, value);
      }

      return instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
