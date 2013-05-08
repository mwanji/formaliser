package co.mewf.formaliser.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

public class Fields {

  public static boolean isCollection(Field candidate) {
    return candidate.getType() == List.class || candidate.getType() == Set.class;
  }

  public static boolean isArray(Field candidate) {
    return candidate.getType().isArray();
  }

  public static Class<?> getComponentClass(Field field) {
    if (isArray(field)) {
      return field.getType().getComponentType();
    }

    if (isCollection(field)) {
      return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    throw new IllegalArgumentException("Field must be an array or a supported collection (List, Set).");
  }

  public static Field[] toFields(Class<?> container, String... fieldNames) {
    Field[] fields = new Field[fieldNames.length];
    for (int i = 0; i < fieldNames.length; i++) {
      try {
        fields[i] = container.getField(fieldNames[i]);
      } catch (Exception e) {
        throw new IllegalArgumentException(container.getName() + " has no field called " + fieldNames[i]);
      }
    }
    return fields;
  }


  private Fields() {}
}
