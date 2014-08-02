/**
 * Thanks so stack overflow:
 * http://stackoverflow.com/a/18804033/3739861
 */
package oneplusoneTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class NullPrintStream extends PrintStream {

  public NullPrintStream() {
    super(new NullByteArrayOutputStream());
  }

  private static class NullByteArrayOutputStream extends ByteArrayOutputStream {

    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
    }

  }

}