// (c) https://github.com/MontiCore/monticore
package cocoTest;

import bindings._cocos.*;
import bindings.util.BindingsError;
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
    return BindingsError.ERROR_CODE_PATTERN;
  }

  protected static final String MODEL_PATH = "src/test/resources/models/cocoTest/";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("validInput")
  void shouldAcceptValidInput(String modelToCheck) {
    //Given
    BindingsCoCoChecker checker = BindingsCoCos.createChecker();

    // When
    checker.checkAll(getAST(MODEL_PATH, modelToCheck));

    // Then
    shouldRejectInvalidInput("", checker, modelToCheck, 0, new BindingsError[] {});
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidInput")
  void shouldRejectInvalidInput(
    //Given
    String testName, BindingsCoCoChecker checker, String modelToCheck,
    int errorCount, BindingsError[] expectedErrors) {

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
        new BindingsCoCoChecker().addCoCo(new ImplementationExists()),
        "missingMT/InvalidBinding.mtb",
        1,
        new BindingsError[] { BindingsError.NO_MODEL_IMPLEMENTATION }
      ),
      Arguments.of(
        "ImplementationHasSamePortsAsInterface",
        new BindingsCoCoChecker().addCoCo(new ImplementationHasSamePortsAsInterface()),
        "implementationPortTest/WrongPortBinding.mtb",
        2,
        new BindingsError[] { BindingsError.NOT_SAME_PORTS_IMPLEMENTED }
      ),
      Arguments.of(
        "InterfaceExists",
        new BindingsCoCoChecker().addCoCo(new InterfaceExists()),
        "missingMT/InvalidBinding.mtb",
        1,
        new BindingsError[] { BindingsError.NO_MODEL_INTERFACE }
      ),
      Arguments.of(
        "LeftSideIsInterface",
        new BindingsCoCoChecker().addCoCo(new LeftSideIsInterface()),
        "interfaceMismatch/WrongModel.mtb",
        2,
        new BindingsError[] { BindingsError.LEFT_SIDE_NO_INTERFACE }
      ),
      Arguments.of(
        "RightSideIsImplementation",
        new BindingsCoCoChecker().addCoCo(new RightSideIsImplementation()),
        "interfaceMismatch/WrongModel.mtb",
        1,
        new BindingsError[] { BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION}
      )
    );
  }
}
