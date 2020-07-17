// (c) https://github.com/MontiCore/monticore
package cocoTest;

import cdlangextension.CDLangExtensionTool;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCoChecker;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension.util.CDLangExtensionError;
import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisArtifactScope;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Tests CoCos of CDLangExtension.
 *
 * @author Julian Krebber
 */
public class CoCoTest extends AbstractTest {
  private static final String PACKAGE = "cocoTest";

  private static final String MODEL_PATH = "src/test/resources/models/";

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
    checker.checkAll(getCDEAST("cocoTest/ImportValid.cde",getCDAST("cocoTest/ImportValid.cd")));
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameEmpty() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getCDEAST("cocoTest/ImportNameNotEmpty.cde",getCDAST("cocoTest/ImportValid.cd")));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.EMPTY_IMPORT_FIELD });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameNotUnique() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getCDEAST("cocoTest/ImportNameUnique.cde",getCDAST("cocoTest/ImportValid.cd")));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_IMPORT_NAME });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importLanguageNotUnique() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getCDEAST("cocoTest/ImportLanguageUnique.cde",getCDAST("cocoTest/ImportValid.cd")));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.AMBIGUOUS_LANGUAGE_NAME });
  }

  /**
   * Tests CoCos that are cd import related.
   */
  @Test
  public void importNameNotExistsInCD() {
    CDLangExtensionCoCoChecker checker = CDLangExtensionCoCos.createChecker();
    checker.checkAll(getCDEAST("cocoTest/ImportNameExists.cde",getCDAST("cocoTest/ImportValid.cd")));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
        new CDLangExtensionError[] { CDLangExtensionError.MISSING_IMPORT_NAME });
  }

  public ASTCDLangExtensionUnit getCDEAST(String fileName, ASTCDCompilationUnit astCD) {
    ASTCDLangExtensionUnit astCDE = null;
    try {
      astCDE = new CDLangExtensionParser().parseCDLangExtensionUnit(MODEL_PATH + fileName).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + MODEL_PATH + fileName + "' CDE artifact was not found");
    }
    Assertions.assertNotNull(astCDE);
    CDLangExtensionTool tool = new CDLangExtensionTool();
    List<File> modelPath = Lists.newArrayList(new File(MODEL_PATH));
    CD4AnalysisArtifactScope artifactScope = tool.createCDSymboltable(astCD,modelPath);
    tool.createCDESymboltable(astCDE, modelPath,artifactScope);
    return astCDE;
  }

  public ASTCDCompilationUnit getCDAST(String fileName) {
    ASTCDCompilationUnit astCD = null;
    try {
      astCD = new CD4AnalysisParser().parseCDCompilationUnit(MODEL_PATH + fileName).orElse(null);
    }
    catch (IOException e) {
      Log.error("File '" + MODEL_PATH + fileName + "' CD artifact was not found");
    }
    Assertions.assertNotNull(astCD);
    return astCD;
  }
}

