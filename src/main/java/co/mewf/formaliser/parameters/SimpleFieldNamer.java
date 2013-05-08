package co.mewf.formaliser.parameters;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.lang.reflect.Member;

import org.apache.commons.lang3.StringUtils;


/**
 * Uses dots to separate field name elements and square brackets for ordered multi-valued fields.
 */
public class SimpleFieldNamer implements FieldNamer {

  private final String root;

  public SimpleFieldNamer() {
    this.root = null;
  }

  public SimpleFieldNamer(Class<?> rootClass) {
    this.root = StringUtils.uncapitalize(rootClass.getSimpleName());
  }

  public SimpleFieldNamer(String root) {
    this.root = root;
  }

  @Override
  public String getName(Member member) {
    return (isNotBlank(root) ? root + "." : "") + member.getName();
  }

  @Override
  public String getName(Member member, int index) {
    return root + "[" + index + "]." + member.getName();
  }

  @Override
  public String getRoot() {
    return root;
  }

  @Override
  public FieldNamer extend(Member member) {
    return new SimpleFieldNamer(getName(member));
  }

  @Override
  public FieldNamer extend(int index) {
    return new SimpleFieldNamer(root + "[" + index + "]");
  }
}
