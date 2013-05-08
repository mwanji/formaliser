package co.mewf.formaliser.testutils.forms;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class Utils {

  public static String read(Class<?> testClass, String fileName) {
    try {
      return IOUtils.toString(testClass.getResourceAsStream(testClass.getSimpleName() + "/" + fileName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Utils() {}
}
