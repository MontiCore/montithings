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
      Arguments.of("valid/ValidBinding.mtb"),
      Arguments.of("implementationPortTest/DifferentPortDeclarationBinding.mtb")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(
        "ImplementationExists",
        new SD4ComponentTestingCoCoChecker().addCoCo(new ImplementationExists()),
        "missingMT/InvalidBinding.mtb",
        1,
        new SD4ComponentTestingError[] { SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION }
      ),
      Arguments.of(
        "ImplementationHasSamePortsAsInterface",
        new SD4ComponentTestingCoCoChecker().addCoCo(new ImplementationHasSamePortsAsInterface()),
        "implementationPortTest/WrongPortBinding.mtb",
        2,
        new SD4ComponentTestingError[] { SD4ComponentTestingError.NOT_SAME_PORTS_IMPLEMENTED }
      ),
      Arguments.of(
        "InterfaceExists",
        new SD4ComponentTestingCoCoChecker().addCoCo(new InterfaceExists()),
        "missingMT/InvalidBinding.mtb",
        1,
        new SD4ComponentTestingError[] { SD4ComponentTestingError.NO_MODEL_INTERFACE }
      ),
      Arguments.of(
        "LeftSideIsInterface",
        new SD4ComponentTestingCoCoChecker().addCoCo(new LeftSideIsInterface()),
        "interfaceMismatch/WrongModel.mtb",
        2,
        new SD4ComponentTestingError[] { SD4ComponentTestingError.LEFT_SIDE_NO_INTERFACE }
      ),
      Arguments.of(
        "RightSideIsImplementation",
        new SD4ComponentTestingCoCoChecker().addCoCo(new RightSideIsImplementation()),
        "interfaceMismatch/WrongModel.mtb",
        1,
        new SD4ComponentTestingError[] { SD4ComponentTestingError.RIGHT_SIDE_NO_IMPLEMENTATION}
      )
    );
  }
}
