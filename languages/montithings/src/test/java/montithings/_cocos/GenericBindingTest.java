// (c) https://github.com/MontiCore/monticore
package montithings._cocos;

import de.se_rwth.commons.logging.Log;
import montithings.cocos.ImplementationFitsInterface;
import montithings.cocos.InterfaceExists;
import montithings.cocos.MontiThingsCoCos;
import montithings.util.MontiThingsError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GenericBindingTest extends AbstractTest {

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
      Arguments.of("cocoTest.genericBindingTest.valid.Assignment")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.interfaceNotFound.Bind",
        1,
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE }
      ),
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.implementationMissing.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.IMPLEMENTATION_MISSING }
      ),
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.interfaceImplementsInterface.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.INTERFACE_IMPLEMENTS_INTERFACE }
      ),
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.notFitsInterface.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.NOT_FITS_INTERFACE }
      ),
      /*
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.genericParameterInterfaceNotFound.Assignment",
        1,
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMTER_INTERFACE_NOT_FOUND }
      ),
       */
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.genericParameterNotFitsInterface.Bind",
        2,
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE,
          MontiThingsError.GENERIC_PARAMTER_NOT_FITS_INTERFACE }
      ),
      Arguments.of(
        new MontiThingsCoCoChecker().addCoCo(new InterfaceExists())
          .addCoCo(new ImplementationFitsInterface()),
        "cocoTest.genericBindingTest.genericParameterNeedsInterface.Bind",
        1,
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMETER_NEEDS_INTERFACE }
      )
    );
  }
}
