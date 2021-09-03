package ps.deployment.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
  
  /**
   * Reads the full {@link InputStream} and returns an array of the read bytes.
   * Note that this function also closes the stream.
   * 
   * @throws IOException
   */
  public static byte[] readAllBytes(InputStream is) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    byte[] buff = new byte[10 * 1024];
    int read;
    while ((read = is.read(buff)) >= 0) {
      baos.write(buff, 0, read);
    }
    is.close();
    
    return baos.toByteArray();
  }
  
}
