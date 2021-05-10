// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class PostcondUsesOnlyOneOutportTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new PostcondUsesOnlyOneOutport());
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.postcondUsesOnlyOneOutport.invalid.Source",
        1,
        new MontiThingsError[] { MontiThingsError.POSTCONDITION_MULTIPLE_OUTPORTS }
      )
    );
  }
}