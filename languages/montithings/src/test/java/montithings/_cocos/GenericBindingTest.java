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
    checker.checkAll(getAST("cocoTest/genericBindingTest/valid/Assignment.mt"));
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  void notInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/interfaceNotFound/Bind.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE });
  }

  @Test
  void implementationMissing() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/implementationMissing/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.IMPLEMENTATION_MISSING });
  }

  @Test
  void interfaceImplementsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/interfaceImplementsInterface/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.INTERFACE_IMPLEMENTS_INTERFACE });
  }

  @Test
  void notFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/notFitsInterface/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_FITS_INTERFACE });
  }

  void genericParameterInterfaceNotFound() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterInterfaceNotFound/Assignment.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.GENERIC_PARAMTER_INTERFACE_NOT_FOUND });
  }

  @Test
  void genericParameterNotFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterNotFitsInterface/Bind.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { MontiThingsError.NOT_INTERFACE,MontiThingsError.GENERIC_PARAMTER_NOT_FITS_INTERFACE });
  }

  @Test
  void genericParameterNeedsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterNeedsInterface/Bind.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] {MontiThingsError.GENERIC_PARAMETER_NEEDS_INTERFACE });
  }
}
