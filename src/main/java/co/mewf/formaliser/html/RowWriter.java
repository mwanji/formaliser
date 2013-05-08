package co.mewf.formaliser.html;


import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

public interface RowWriter {

  public static class RowInfo {
    public String type;
    public String label;
    public String name;
    public String value;
    public Extras extras;

    public RowInfo(String type, String label, String name, String value, Extras extras) {
      this.type = type;
      this.label = label;
      this.name = name;
      this.value = value;
      this.extras = extras;
    }
  }

  void write(RowInfo rowInfo, Field field, Writer writer) throws IOException;
}
