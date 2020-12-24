// (c) https://github.com/MontiCore/monticore
package cocoTest;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import mtconfig.MTConfigTool;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCoChecker;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._parser.MTConfigParser;
import mtconfig.util.MTConfigError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests CoCos of MTConfig.
 *
 * @author Julian Krebber
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
    MTConfigCoCoChecker checker = MTConfigCoCos.createChecker();
    checker.checkAll(getAST("cocoTest/valid/SpeedLimiter.mtcfg"));
    if (Log.getErrorCount() != 0) {
      Log.getFindings().stream().filter(Finding::isError).forEach(f -> System.err.println(f.getMsg()));
    }
    assertThat(Log.getErrorCount()).isEqualTo(0);
  }

  public ASTMTConfigUnit getAST(String fileName) {
    ASTMTConfigUnit astMTCFG = null;
    try {
      Path filePath = Paths.get(MODEL_PATH + fileName);
      astMTCFG = new MTConfigParser().parseMTConfigUnit(filePath.toFile().getPath()).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + MODEL_PATH + fileName + "' MTCFG artifact was not found");
    }
    Assertions.assertNotNull(astMTCFG);
    MTConfigTool tool = new MTConfigTool();
    tool.createSymboltable(astMTCFG, new File(MODEL_PATH));
    return astMTCFG;
  }
}

