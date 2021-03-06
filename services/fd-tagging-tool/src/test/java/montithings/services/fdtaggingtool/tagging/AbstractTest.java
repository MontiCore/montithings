// (c) https://github.com/MontiCore/monticore
package montithings.services.fdtaggingtool.tagging;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import montithings.services.fdtaggingtool.tagging._ast.ASTTagging;
import montithings.services.fdtaggingtool.tagging._cocos.TaggingCoCoChecker;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTest {
  protected static final String EXAMPLES_PATH = "src/test/resources/";

  protected final static String VALID_PATH = EXAMPLES_PATH + "valid/";

  protected final static String INVALID_PATH = EXAMPLES_PATH + "invalid/";

  protected TaggingTool tgTool;

  protected TaggingCoCoChecker checker;

  public AbstractTest() {
    Log.enableFailQuick(false);
  }

  @BeforeAll
  public static void initMill() {
    TaggingMill.init();
  }

  @BeforeEach
  public void setup() {
    this.tgTool = new TaggingTool();

    this.checker = new TaggingCoCoChecker();

    initCoCoChecker();

    Log.getFindings().clear();
  }

  protected abstract void initCoCoChecker();

  protected abstract List<String> getErrorCodeOfCocoUnderTest();

  public void testCorrectExamples(String modelName) {
    ASTTagging tagging = tgTool.loadModel(VALID_PATH + modelName);
    String msgs = Log.getFindings().stream().map(Finding::getMsg)
      .collect(Collectors.joining(System.lineSeparator()));
    Assertions.assertEquals(0, Log.getErrorCount(), msgs);
    Assertions.assertEquals(0,
      Log.getFindings()
        .stream()
        .map(Finding::buildMsg)
        .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
        .count(),
      msgs);
  }

  protected void testCocoViolation(String modelDirectory, int errorCount, int logFindingsCount) {
    ASTTagging tagging = tgTool.loadModel(INVALID_PATH + modelDirectory);
    Assertions.assertEquals(errorCount, Log.getErrorCount());
    Assertions.assertEquals(logFindingsCount,
      Log.getFindings()
        .stream()
        .map(Finding::buildMsg)
        .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
        .count());
  }

  public void testValidToolCombination(String modelFile, String configurationFile) {
    boolean result = tgTool.isFeatureDeployable(VALID_PATH + modelFile, "AntiFire");
    //List<ASTFeatureConfiguration> activatedComponents = tgTool.findMaximalDeployableConfiguration(VALID_PATH + modelFile);
    //tgTool.updateIoTManager(VALID_PATH + modelFile, VALID_PATH + configurationFile);
    Assertions.assertTrue(true);
  }

  public void testInvalidToolCombination(String modelFile) {
    boolean result;
    //result = tgTool.isFeatureDeployable(INVALID_PATH + modelFile);
    //Assertions.assertFalse(result);
  }
}
