// (c) https://github.com/MontiCore/monticore
package montithings._cocos;

import montithings.cocos.ImplementationFitsInterface;
import montithings.cocos.InterfaceExists;
import montithings.util.MontiThingsError;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class GenericBindingTest extends AbstractCoCoTest {

  protected static MontiThingsCoCoChecker getChecker() {
    return new MontiThingsCoCoChecker()
      .addCoCo(new InterfaceExists())
      .addCoCo(new ImplementationFitsInterface());
  }

  protected static Stream<Arguments> validInput() {
    return Stream.of(
      Arguments.of("cocoTest.genericBindingTest.valid.Assignment"),
      Arguments.of("cocoTest.SIUnits")
    );
  }

  protected static Stream<Arguments> invalidInput() {
    return Stream.of(
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.interfaceNotFound.Bind",
        1,
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE }
      ),
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.implementationMissing.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.IMPLEMENTATION_MISSING }
      ),
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.interfaceImplementsInterface.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.INTERFACE_IMPLEMENTS_INTERFACE }
      ),
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.notFitsInterface.Assignment",
        2,
        new MontiThingsError[] { MontiThingsError.NOT_FITS_INTERFACE }
      ),
      /*
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.genericParameterInterfaceNotFound.Assignment",
        1,
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMTER_INTERFACE_NOT_FOUND }
      ),
       */
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.genericParameterNotFitsInterface.Bind",
        2,
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE,
          MontiThingsError.GENERIC_PARAMTER_NOT_FITS_INTERFACE }
      ),
      Arguments.of(getChecker(),
        "cocoTest.genericBindingTest.genericParameterNeedsInterface.Bind",
        1,
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMETER_NEEDS_INTERFACE }
      )
    );
  }
}
