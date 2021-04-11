package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class MaxOneUpdateIntervalTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new MaxOneUpdateInterval());
  }

  protected static MontiThingsError[] getExpectedErrors() {
    return new MontiThingsError[] { MontiThingsError.ONLY_ONE_UPDATE_INTERVAL };
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.maxOneUpdateInterval.invalid.Source",
        1,
        getExpectedErrors()
      )
    );
  }
}