package fortune_grpc;

import fortune.Fortune.GetFortuneRequest;
import fortune.Fortune.GetFortuneResponse;
import fortune.FortuneServiceGrpc.FortuneServiceImplBase;
import io.grpc.stub.StreamObserver;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

public class FortuneService extends FortuneServiceImplBase {
   private static Map<Integer, String> FORTUNE_NUMBER_TO_MESSAGE =
     new LinkedHashMap<Integer, String>() {{
       put(0, "You laugh now, wait till you get home.");
       put(1, "Wouldn't it be ironic...to die in the living room?");
       put(2, "About time I got out of that cookie.");
       put(3, "Your resemblance to a muppet will prevent the world from taking you seriously.");
       put(4, "You will be hungry again in one hour.");
       put(5, "run.");
     }};

  @Override
  public void getFortune(GetFortuneRequest request, StreamObserver<GetFortuneResponse> responseObserver) {
     if (!FORTUNE_NUMBER_TO_MESSAGE.containsKey(request.getFortuneNumber())) {
       throw new IllegalStateException(format("invalid fortune number: %s", request.getFortuneNumber()));
     }

     responseObserver.onNext(
       GetFortuneResponse.newBuilder()
         .setFortuneContent(FORTUNE_NUMBER_TO_MESSAGE.get(request.getFortuneNumber()))
         .build()
     );
     responseObserver.onCompleted();
  }
}
