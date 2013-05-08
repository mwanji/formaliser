package co.mewf.formaliser.parameters;

import java.lang.reflect.Member;

public interface FieldNamer {

  String getName(Member member);
  String getName(Member member, int index);
  FieldNamer extend(Member member);
  FieldNamer extend(int index);
  String getRoot();
}
