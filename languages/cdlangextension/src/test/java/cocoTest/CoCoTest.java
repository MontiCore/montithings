// (c) https://github.com/MontiCore/monticore
package cocoTest;

import cdlangextension.CDLangExtensionTool;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension.util.CDLangExtensionError;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Tests CoCos of CDLangExtension.
 *
 * @author Julian Krebber
 */
public class CoCoTest extends AbstractTest {
  private static final String PACKAGE = "cocoTest";

  private static final String MODEL_PATH = "src/test/resources/models/cocoTest/";

  @Override
  protected Pattern supplyErrorCodePattern() {
    return CDLangExtensionError.ERROR_CODE_PATTERN;
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void valid() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getAST(MODEL_PATH, "ImportValid/ImportValid.cde"));
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameEmpty() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getAST(MODEL_PATH, "ImportNameNotEmpty/ImportNameNotEmpty.cde"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.EMPTY_IMPORT_FIELD });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameNotUnique() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getAST(MODEL_PATH, "ImportNameUnique/ImportNameUnique.cde"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_IMPORT_NAME });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importLanguageNotUnique() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getAST(MODEL_PATH, "ImportLanguageUnique/ImportLanguageUnique.cde"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_LANGUAGE_NAME });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameNotExistsInCD() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getAST(MODEL_PATH, "ImportNameExists/ImportNameExists.cde"));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.MISSING_IMPORT_NAME });
  }

  public ASTCDLangExtensionUnit getAST(String modelPath, String fileName) {
    ASTCDLangExtensionUnit astCDE = null;
    try {
      astCDE = new CDLangExtensionParser().parseCDLangExtensionUnit(modelPath + fileName).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + modelPath + fileName + "' CDE artifact was not found");
    }
    Assertions.assertNotNull(astCDE);
    CDLangExtensionTool tool = new CDLangExtensionTool();
    tool.createSymboltable(astCDE, new File(modelPath));
    Log.init();
    LogStub.init();
    return astCDE;
  }
}

