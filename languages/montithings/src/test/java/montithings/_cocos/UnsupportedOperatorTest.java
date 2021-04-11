// (c) https://github.com/MontiCore/monticore
package montithings._cocos;

import montithings.cocos.UnsupportedOperator;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class UnsupportedOperatorTest extends AbstractCoCoTest {

  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new UnsupportedOperator());
  }

  protected static MontiThingsError[] getExpectedErrors() {
    return new MontiThingsError[] { MontiThingsError.UNSUPPORTED_OPERATOR };
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.unsupportedOperator.invalidSimilar.Source",
        1,
        getExpectedErrors()
      ),
      Arguments.of(getChecker(),
        "cocoTest.unsupportedOperator.invalidNotSimilar.Source",
        1,
        getExpectedErrors()
      )
    );
  }
}
