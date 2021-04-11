package montithings.cocos;

import montithings._cocos.MontiThingsCoCoChecker;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class PublishReferencesPortTest extends AbstractCoCoTest {
  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new PublishReferencesPort());
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.valid.Source")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.publishReferencesPort.invalid.Source",
        1,
        new MontiThingsError[] { MontiThingsError.PUBLISH_IDENTIFIER_UNKNOWN }
      )
    );
  }
}