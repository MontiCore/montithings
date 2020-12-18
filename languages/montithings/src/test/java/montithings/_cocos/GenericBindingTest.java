// (c) https://github.com/MontiCore/monticore
package montithings._cocos;

import de.se_rwth.commons.logging.Log;
import montithings.cocos.ImplementationFitsInterface;
import montithings.cocos.InterfaceExists;
import montithings.cocos.MontiThingsCoCos;
import montithings.util.MontiThingsError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class GenericBindingTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }

  @Test
  void checkValidGenericBindingTest()  {
    MontiThingsCoCoChecker checker = MontiThingsCoCos.createChecker();
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.valid.Assignment").getAstNode());
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  void notInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.interfaceNotFound.Bind").getAstNode());
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE });
  }

  @Test
  void implementationMissing() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.implementationMissing.Assignment").getAstNode());
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.IMPLEMENTATION_MISSING });
  }

  @Test
  void interfaceImplementsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.interfaceImplementsInterface.Assignment").getAstNode());
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.INTERFACE_IMPLEMENTS_INTERFACE });
  }

  @Test
  void notFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.notFitsInterface.Assignment").getAstNode());
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_FITS_INTERFACE });
  }

  void genericParameterInterfaceNotFound() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.genericParameterInterfaceNotFound.Assignment").getAstNode());
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMTER_INTERFACE_NOT_FOUND });
  }

  @Test
  void genericParameterNotFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.genericParameterNotFitsInterface.Bind").getAstNode());
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE,MontiThingsError.GENERIC_PARAMTER_NOT_FITS_INTERFACE });
  }

  @Test
  void genericParameterNeedsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getSymbol("cocoTest.genericBindingTest.genericParameterNeedsInterface.Bind").getAstNode());
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] {MontiThingsError.GENERIC_PARAMETER_NEEDS_INTERFACE });
  }
}
