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

    BindingsCoCoChecker implExistsChecker = new BindingsCoCoChecker();
    implExistsChecker.addCoCo(new ImplementationExists());

    BindingsCoCoChecker implementationHasSamePortsAsInterfaceChecker = new BindingsCoCoChecker();
    implementationHasSamePortsAsInterfaceChecker.addCoCo(new ImplementationHasSamePortsAsInterface());

    BindingsCoCoChecker interfaceExistsChecker = new BindingsCoCoChecker();
    interfaceExistsChecker.addCoCo(new InterfaceExists());

    BindingsCoCoChecker leftSideIsInterfaceChecker = new BindingsCoCoChecker();
    leftSideIsInterfaceChecker.addCoCo(new LeftSideIsInterface());

    BindingsCoCoChecker rightSideIsImplementationChecker = new BindingsCoCoChecker();
    rightSideIsImplementationChecker.addCoCo(new RightSideIsImplementation());

    return Stream.of(
      Arguments.of(
        "ImplementationExists",
        implExistsChecker,
        "missingMT/InvalidBinding.mtb",
        1,
        new BindingsError[] { BindingsError.NO_MODEL_IMPLEMENTATION }
      ),
      Arguments.of(
        "ImplementationHasSamePortsAsInterface",
        implementationHasSamePortsAsInterfaceChecker,
        "implementationPortTest/WrongPortBinding.mtb",
        2,
        new BindingsError[] { BindingsError.NOT_SAME_PORTS_IMPLEMENTED }
      ),
      Arguments.of(
        "InterfaceExists",
        interfaceExistsChecker,
        "missingMT/InvalidBinding.mtb",
        1,
        new BindingsError[] { BindingsError.NO_MODEL_INTERFACE }
      ),
      Arguments.of(
        "LeftSideIsInterface",
        leftSideIsInterfaceChecker,
        "interfaceMismatch/WrongModel.mtb",
        2,
        new BindingsError[] { BindingsError.LEFT_SIDE_NO_INTERFACE }
      ),
      Arguments.of(
        "RightSideIsImplementation",
        rightSideIsImplementationChecker,
        "interfaceMismatch/WrongModel.mtb",
        1,
        new BindingsError[] { BindingsError.RIGHT_SIDE_NO_IMPLEMENTATION}
      )
    );
  }
}
