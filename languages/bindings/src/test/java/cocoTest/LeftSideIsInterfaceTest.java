// (c) https://github.com/MontiCore/monticore
package cocoTest;

import bindings._cocos.BindingsCoCoChecker;
import bindings._cocos.LeftSideIsInterface;
import bindings.util.BindingsError;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class LeftSideIsInterfaceTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return BindingsError.ERROR_CODE_PATTERN;
  }

  protected static final String MODEL_PATH = "src/test/resources/models/cocoTest/";

  @Test
  void shouldFailWithInvalidBinding() {
    BindingsCoCoChecker checker = new BindingsCoCoChecker().addCoCo(new LeftSideIsInterface());
    checker.checkAll(getAST(MODEL_PATH,"interfaceMismatch/WrongModel.mtb"));
    Assertions.assertEquals(2, Log.getErrorCount());
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new BindingsError[] { BindingsError.LEFT_SIDE_NO_INTERFACE});
  }
}
