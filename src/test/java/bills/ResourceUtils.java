package bills;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

class ResourceUtils {

  public static String read(final String resourceName, final ClassLoader classLoader) throws URISyntaxException, IOException {

    final URI resourceURI = classLoader.getResource(resourceName).toURI();
    final File resourceFile = new File(resourceURI);
    return Files.toString(resourceFile, Charset.defaultCharset());
  }

  private ResourceUtils() {
  }

}
