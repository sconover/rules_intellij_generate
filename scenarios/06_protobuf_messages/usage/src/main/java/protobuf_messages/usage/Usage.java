package protobuf_messages.usage;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import html_email.HtmlEmailOuterClass.HtmlEmail;
import plain_email.PlainEmailOuterClass.Identity;
import plain_email.PlainEmailOuterClass.PlainEmail;

import java.util.List;

import static java.lang.String.format;

class Usage {
  public PlainEmail makePlainEmail() {
    return PlainEmail.newBuilder()
      .setFrom(Identity.newBuilder()
        .setName("Mom")
        .setEmail("mom@example.com")
        .build())
      .setTo(Identity.newBuilder()
        .setName("Kid")
        .setEmail("kid@example.com")
        .build())
      .setBodyText("hi how are you doing today?")
      .build();
  }

  public HtmlEmail makeHtmlEmail() {
    PlainEmail plainEmail = makePlainEmail();
    return HtmlEmail.newBuilder()
      .setBodyHtml(format("<html>%s</html>", plainEmail.getBodyText()))
      .setPlainEmail(plainEmail)
      .build();
  }
}
