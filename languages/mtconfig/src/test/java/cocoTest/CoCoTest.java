// (c) https://github.com/MontiCore/monticore
package cocoTest;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import mtconfig.MTConfigTool;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCoChecker;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig.util.MTConfigError;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests CoCos of MTConfig.
 */
public class CoCoTest extends AbstractTest {
  protected static final String PACKAGE = "cocoTest";

  protected static final String MODEL_PATH = "src/test/resources/models/";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MTConfigError.ERROR_CODE_PATTERN;
  }

  @Test
  public void valid() {
    // Given
    MTConfigCoCoChecker checker = MTConfigCoCos.createChecker();
    MTConfigTool tool = new MTConfigTool();
    tool.initSymbolTable(new File(MODEL_PATH));
    ASTMTConfigUnit ast = tool.processFile(MODEL_PATH + "cocoTest/valid/SpeedLimiter.mtcfg");

    // When
    checker.checkAll(ast);

    // Then
    if (Log.getErrorCount() != 0) {
      Log.getFindings().stream().filter(Finding::isError).forEach(f -> System.err.println(f.getMsg()));
    }
    assertThat(Log.getErrorCount()).isEqualTo(0);
  }
}

