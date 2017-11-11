package fortune_grpc;

import com.google.protobuf.Descriptors;
import fortune.Fortune;
import fortune.Fortune.GetFortuneRequest;
import fortune.Fortune.GetFortuneResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FortuneServiceTest {
  @Test
  public void get_fortune() {
    List<GetFortuneResponse> results = new ArrayList<>();
    FortuneService fortuneService = new FortuneService();

    FakeStreamObserver<GetFortuneResponse> responseObserver = new FakeStreamObserver<>();
    fortuneService.getFortune(GetFortuneRequest.newBuilder()
        .setFortuneNumber(4)
        .build(),
      responseObserver);

    assertEquals(
      asList("You will be hungry again in one hour."),
      responseObserver.results.stream().map(GetFortuneResponse::getFortuneContent).collect(toList()));


    GetFortuneResponse actual = responseObserver.results.get(0);
    // demonstration of retrieval of an option
    Map.Entry<Descriptors.FieldDescriptor, Object> optionEntry =
      actual.getDescriptorForType().findFieldByNumber(Fortune.Truth.IS_THE_FORTUNE_ACTUALLY_REAL_FIELD_NUMBER)
        .getOptions().getAllFieldsRaw().entrySet().iterator().next();

    assertEquals("the_truth", optionEntry.getKey().getName());
    assertEquals(Fortune.Truth.newBuilder().setIsTheFortuneActuallyReal(true).build(), optionEntry.getValue());
  }

  class FakeStreamObserver<T> implements StreamObserver<T> {
    public List<T> results = new ArrayList<>();

    @Override
    public void onNext(T value) {
      results.add(value);
    }

    @Override
    public void onError(Throwable t) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void onCompleted() {
      // no-op
    }
  }
}
