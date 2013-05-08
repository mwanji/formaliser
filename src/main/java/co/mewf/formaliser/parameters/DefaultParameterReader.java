package co.mewf.formaliser.parameters;

import static co.mewf.formaliser.utils.Fields.getComponentClass;
import static co.mewf.formaliser.utils.Fields.isArray;
import static co.mewf.formaliser.utils.Fields.isCollection;
import co.mewf.formaliser.utils.Fields;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.convert.StringConvert;

public class DefaultParameterReader {

  public boolean handles(Field field) {
    return isConvertible(field.getType()) || isConvertibleArray(field) || isConvertibleCollection(field);
  }

  public Object handle(Field field, String parameterName, Map<String, List<String>> parameterValues) {
    if (parameterValues == null || parameterValues.isEmpty()) {
      return null;
    }

    if (isArray(field)) {
      Class<?> arrayComponentType = Fields.getComponentClass(field);

      List<String> values = extractValues(parameterName, parameterValues);

      if (values == null) {
        return null;
      }

      Object array = Array.newInstance(arrayComponentType, values.size());
      for (int i = 0; i < values.size(); i++) {
        Array.set(array, i, StringConvert.INSTANCE.convertFromString(arrayComponentType, values.get(i)));
      }

      return array;
    }

    if (isCollection(field)) {
      Class<?> collectionType = Fields.getComponentClass(field);

      Collection<Object> collection = field.getType() == List.class ? new ArrayList<Object>() : new HashSet<Object>();

      List<String> values = extractValues(parameterName, parameterValues);

      if (values == null) {
        return null;
      }

      for (int i = 0; i < values.size(); i++) {
        collection.add(StringConvert.INSTANCE.convertFromString(collectionType, values.get(i)));
      }

      return collection;
    }

    List<String> values = parameterValues.get(parameterName);

    if (values == null || values.isEmpty()) {
      return null;
    }

    String parameterValue = parameterValues.get(parameterName).get(0);

    return StringConvert.INSTANCE.convertFromString(field.getType(), parameterValue);
  }

  private List<String> extractValues(String parameterName, Map<String, List<String>> parameterValues) {
    List<String> values = null;
    if (parameterValues.containsKey(parameterName)) {
      values = parameterValues.get(parameterName);
    } else {
      Pattern pattern = Pattern.compile(parameterName + "\\[(\\d)\\]");
      for (Map.Entry<String, List<String>> entry : parameterValues.entrySet()) {
        Matcher matcher = pattern.matcher(entry.getKey());
        if (matcher.matches()) {
          if (values == null) {
            values = new ArrayList<String>();
          }
          int index = Integer.parseInt(matcher.group(1));
          while (index >= values.size()) {
            values.add(null);
          }
          values.set(index, entry.getValue().get(0));
        }
      }
    }
    return values;
  }

  private boolean isConvertibleCollection(Field field) {
    return isCollection(field) && isConvertible(getComponentClass(field));
  }

  private boolean isConvertibleArray(Field field) {
    return isArray(field) && isConvertible(getComponentClass(field));
  }

  private boolean isConvertible(Class<?> fieldClass) {
    try {
      StringConvert.INSTANCE.findConverter(fieldClass);
      return true;
    } catch (IllegalStateException e) {
      return false;
    }
  }
}
