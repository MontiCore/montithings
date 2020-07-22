// (c) https://github.com/MontiCore/monticore
package cocoTest;

import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.BindingsCoCos;
import bindings._cocos.ImplementationExists;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class ImplementationExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return BindingsError.ERROR_CODE_PATTERN;
  }

  @Test
  void shouldAcceptValidBinding() {
    BindingsCoCoChecker checker = BindingsCoCos.createChecker();
    checker.checkAll(getAST("cocoTest/valid/ValidBinding.mtb"));
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  void shouldFailWithInvalidBinding() {
    BindingsCoCoChecker checker = new BindingsCoCoChecker().addCoCo(new ImplementationExists());
    checker.checkAll(getAST("cocoTest/missingMT/InvalidBinding.mtb"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new BindingsError[] { BindingsError.NO_MODEL_IMPLEMENTATION });
  }
}
