package co.mewf.formaliser.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Extras {

  public static final Extras NONE = new Extras() {
    @Override
    public Extras a(String attribute, Object value) {
      throw new UnsupportedOperationException("Extras.NONE is immutable.");
    };

    @Override
    public Extras d(String name, Object value) {
      throw new UnsupportedOperationException("Extras.NONE is immutable.");
    };
  };

  private final List<Object> attributes = new ArrayList<Object>();
  private String type;

  public static Extras type(String type) {
    Extras extras = new Extras();
    extras.type = type;
    
    return extras;
  }

  public static Extras data(String name, Object value) {
    return attr("data-" + name, value);
  }

  public static Extras attr(String name, Object value) {
    return new Extras().a(name, value);
  }

  public Extras a(String name, Object value) {
    attributes.add(name);
    attributes.add(value);
    return this;
  }

  public Extras d(String name, Object value) {
    return a("data-" + name, value);
  }

  public boolean hasType() {
    return type != null;
  }
  
  public String getType() {
    return type;
  }

  public Extras copy() {
    Extras extras = new Extras();
    extras.attributes.addAll(this.attributes);
    return extras;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Iterator it = attributes.iterator(); it.hasNext();) {
      builder.append(" ").append(it.next()).append("=\"").append(it.next()).append('"');
    }

    return builder.toString();
  }

  private Extras() {}
}
