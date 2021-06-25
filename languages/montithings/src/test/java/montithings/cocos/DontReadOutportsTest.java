// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import montithings._cocos.MontiThingsASTBehaviorCoCo;
import montithings._cocos.MontiThingsCoCoChecker;
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
    MontiThingsCoCoChecker cocos = new MontiThingsCoCoChecker();
    cocos.addCoCo((MontiThingsASTBehaviorCoCo) new DontReadOutports());

    return Stream.of(
      Arguments.of(
        cocos,
        "cocoTest.dontReadOutports.invalidBehavior.Source",
        1,
        new MontiThingsError[] { MontiThingsError.OUTPORT_WRITE_ONLY }
      ),
      Arguments.of(
        cocos,
        "cocoTest.dontReadOutports.invalidEvery.Source",
        1,
        new MontiThingsError[] { MontiThingsError.OUTPORT_WRITE_ONLY }
      )
    );
  }
}