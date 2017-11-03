package annotation_processor.usage;

import org.junit.jupiter.api.Test;
import somepackage.ThisIsAClassname;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UsageTest {
  @Test
  public void gen_class() {
    assertEquals(77, new ThisIsAClassname().foo);
  }

  @Test
  public void gen_text_file() throws Exception {
    URL resource = getClass().getClassLoader().getResource("generated/text_files/this_is_a_text_file.txt");
    assertNotNull(resource);
    String content = new String(Files.readAllBytes(Paths.get(resource.toURI())));
    assertEquals("content of text file this_is_a_text_file.txt\n", content);
  }
}
