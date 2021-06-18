// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.cocoTest;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4ComponentTestingNode;
import de.monticore.lang.sd4componenttesting._cocos.*;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CoCoTest extends AbstractTest {
  @Override
  protected Pattern supplyErrorCodePattern() {
    return SD4ComponentTestingError.ERROR_CODE_PATTERN;
  }

  protected static final String MODEL_PATH = "src/test/resources/examples/cocoTest/";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("validInput")
  void shouldAcceptValidInput(String modelToCheck) {
    //Given
    SD4ComponentTestingCoCoChecker checker = SD4ComponentTestingCoCos.createChecker();

    // When
    checker.checkAll(getAST(MODEL_PATH, modelToCheck));

    // Then
    shouldRejectInvalidInput("", checker, modelToCheck, 0, new SD4ComponentTestingError[] {});
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidInput")
  void shouldRejectInvalidInput(
    //Given
    String testName, SD4ComponentTestingCoCoChecker checker, String modelToCheck,
    int errorCount, SD4ComponentTestingError[] expectedErrors) {

    // When
    checker.checkAll(getAST(MODEL_PATH, modelToCheck));

    // Then
    Assertions.assertEquals(errorCount, Log.getErrorCount());
    this.checkExpectedErrorsPresent(Log.getFindings(), expectedErrors);
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("valid/MainTest.sd4c")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of();
  }
}
