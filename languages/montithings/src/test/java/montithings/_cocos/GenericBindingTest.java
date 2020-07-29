package montithings._cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMontiThingsNode;
import montithings.cocos.ImplementationFitsInterface;
import montithings.cocos.InterfaceExists;
import montithings.cocos.MontiThingsCoCos;
import montithings.util.MontiThingsError;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class GenericBindingTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiThingsError.ERROR_CODE_PATTERN;
  }

  /*@Ignore
  @Test
  void checkValidGenericBindingTest()  {
    MontiThingsCoCoChecker checker = MontiThingsCoCos.createChecker();
    checker.checkAll(getAST("cocoTest/genericBindingTest/valid/Assignment.mt"));
    Assertions.assertEquals(0, Log.getErrorCount());
  }*/

  /*@Test
  void notInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/interfaceNotFound/Bind.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT141" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void implementationMissing() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/implementationMissing/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT143" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void interfaceImplementsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/interfaceImplementsInterface/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT144" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void notFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/notFitsInterface/Assignment.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT145" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void genericParameterInterfaceNotFound() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterInterfaceNotFound/Assignment.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT146" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void genericParameterNotFitsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterNotFitsInterface/Bind.mt"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT147","xMT141" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }

  @Test
  void genericParameterNeedsInterface() {
    MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker().addCoCo(new InterfaceExists()).addCoCo(new ImplementationFitsInterface());
    checker.checkAll(getAST("cocoTest/genericBindingTest/genericParameterNeedsInterface/Bind.mt"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new MontiThingsError[] { TODO "xMT148" MontiThingsError.NO_MODEL_IMPLEMENTATION });
  }*/
}
