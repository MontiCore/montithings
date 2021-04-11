package montithings._cocos;

import de.se_rwth.commons.logging.Log;
import montithings.cocos.DontReadOutports;
import montithings.cocos.MontiThingsCoCos;
import montithings.util.MontiThingsError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class DontReadOutportsTest extends AbstractTest {
  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("validInput")
  void shouldAcceptValidInput(String symbolName) {
    // Accepting means not rejecting when testing against all CoCos
    MontiThingsCoCoChecker checker = MontiThingsCoCos.createChecker();
    shouldRejectInvalidInput(checker, symbolName, 0, new MontiThingsError[] {});
  }

  @ParameterizedTest(name = "[{index}] {1}, {2}")
  @MethodSource("invalidInput")
  public void shouldRejectInvalidInput(
    // Given
    MontiThingsCoCoChecker checker, String symbolName,
    int expectedErrorCount, MontiThingsError[] expectedFindings) {

    // When
    checker.checkAll(getSymbol(symbolName).getAstNode());

    // Then
    Assertions.assertEquals(expectedErrorCount, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), expectedFindings);
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.dontReadOutports.valid.Source")
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