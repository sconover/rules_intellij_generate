package protobuf_messages.usage;

import html_email.HtmlEmailOuterClass.HtmlEmail;
import org.junit.jupiter.api.Test;
import plain_email.PlainEmailOuterClass.Identity;
import plain_email.PlainEmailOuterClass.PlainEmail;

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
      new Usage().makeHtmlEmail());
  }
}
