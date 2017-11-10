package protobuf_messages.usage;

import com.google.protobuf.Descriptors;
import html_email.HtmlEmailOuterClass;
import html_email.HtmlEmailOuterClass.HtmlEmail;
import html_email.HtmlEmailOuterClass.SearchIndex;
import org.junit.jupiter.api.Test;
import plain_email.PlainEmailOuterClass.Identity;
import plain_email.PlainEmailOuterClass.PlainEmail;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsageTest {
  @Test
  public void plain_email() {
    assertEquals(PlainEmail.newBuilder()
        .setFrom(Identity.newBuilder()
          .setName("Mom")
          .setEmail("mom@example.com")
          .build())
        .setTo(Identity.newBuilder()
          .setName("Kid")
          .setEmail("kid@example.com")
          .build())
        .setBodyText("hi how are you doing today?")
        .build(),
      new Usage().makePlainEmail());
  }

  @Test
  public void html_email() {
    HtmlEmail actual = new Usage().makeHtmlEmail();
    assertEquals(HtmlEmail.newBuilder()
        .setBodyHtml("<html>hi how are you doing today?</html>")
        .setPlainEmail(PlainEmail.newBuilder()
          .setFrom(Identity.newBuilder()
            .setName("Mom")
            .setEmail("mom@example.com")
            .build())
          .setTo(Identity.newBuilder()
            .setName("Kid")
            .setEmail("kid@example.com")
            .build())
          .setBodyText("hi how are you doing today?")
          .build())
        .build(),
      actual);

    // demonstration of retrieval of an option
    Map.Entry<Descriptors.FieldDescriptor, Object> optionEntry =
      actual.getDescriptorForType().findFieldByNumber(HtmlEmail.BODY_HTML_FIELD_NUMBER)
        .getOptions().getAllFieldsRaw().entrySet().iterator().next();

    assertEquals("search_index", optionEntry.getKey().getName());
    assertEquals(SearchIndex.newBuilder().setShouldIndex(true).build(), optionEntry.getValue());
  }
}
