package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class LoggedVariablesAreResolvableTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new LoggedVariablesAreResolvable());
  }

  protected static MontiThingsError[] getExpectedErrors() {
    return new MontiThingsError[] { MontiThingsError.LOG_IDENTIFIER_UNKNOWN };
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.loggedVariablesAreResolvable.invalid.Source",
        1,
        getExpectedErrors()
      )
    );
  }
}