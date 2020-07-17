// (c) https://github.com/MontiCore/monticore
package cocoTest;

import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.ImplementationHasSamePortsAsInterface;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class ImplementationHasSamePortsAsInterfaceTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return BindingsError.ERROR_CODE_PATTERN;
  }

  @Test
  void shouldFailWithNotSamePortsImplemented() {
    BindingsCoCoChecker checker = new BindingsCoCoChecker().addCoCo(new ImplementationHasSamePortsAsInterface());
    checker.checkAll(getAST("cocoTest/implementationPortTest/WrongPortBinding.mtb"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new BindingsError[] { BindingsError.NOT_SAME_PORTS_IMPLEMENTED });
  }

  @Test
  void shouldAcceptDifferentPortDeclaration() {
    BindingsCoCoChecker checker = new BindingsCoCoChecker().addCoCo(new ImplementationHasSamePortsAsInterface());
    checker.checkAll(getAST("cocoTest/implementationPortTest/DifferentPortDeclarationBinding.mtb"));
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}
