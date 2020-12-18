// (c) https://github.com/MontiCore/monticore
package cocoTest;

import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.InterfaceExists;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class InterfaceExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return BindingsError.ERROR_CODE_PATTERN;
  }

  protected static final String MODEL_PATH = "src/test/resources/models/cocoTest/";

  @Test
  void shouldFailWithInvalidBinding() {
    BindingsCoCoChecker checker = new BindingsCoCoChecker().addCoCo(new InterfaceExists());
    checker.checkAll(getAST(MODEL_PATH, "missingMT/InvalidBinding.mtb"));
    Assertions.assertEquals(1, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new BindingsError[] { BindingsError.NO_MODEL_INTERFACE });
  }

}
