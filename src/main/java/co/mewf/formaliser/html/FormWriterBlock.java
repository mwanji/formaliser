package co.mewf.formaliser.html;

import co.mewf.formaliser.FormWriter;

import java.io.Writer;

public interface FormWriterBlock<T> {
  void write(FormWriter itemFormWriter, T item, Writer writer) throws Exception;
}
