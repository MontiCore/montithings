package montithings._cocos;

import montithings.cocos.DontReadOutports;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class DontReadOutportsTest extends AbstractCoCoTest {

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo((MontiThingsASTBehaviorCoCo) new DontReadOutports()),
        "cocoTest.dontReadOutports.invalidBehavior.Source",
        1,
        new MontiThingsError[] { MontiThingsError.OUTPORT_WRITE_ONLY }
      ),
      Arguments.of(
        new MontiThingsCoCoChecker()
          .addCoCo((MontiThingsASTMTEveryBlockCoCo) new DontReadOutports()),
        "cocoTest.dontReadOutports.invalidEvery.Source",
        1,
        new MontiThingsError[] { MontiThingsError.OUTPORT_WRITE_ONLY }
      )
    );
  }
}