// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting;

import montithings.tools.sd4componenttesting._ast.ASTSD4Artifact;
import montithings.tools.sd4componenttesting._cocos.SD4ComponentTestingCoCoChecker;
import montithings.tools.sd4componenttesting._parser.SD4ComponentTestingParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._parser.SD4ComponentTestingParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

public abstract class AbstractCoCoTest {
  protected static final String SYMBOL_PATH = "src/test/resources";
  protected final static String CORRECT_PATH = SYMBOL_PATH + "/examples/correct/";
  protected final static String INCORRECT_PATH = SYMBOL_PATH + "/examples/incorrect/";

  protected final SD4ComponentTestingParser parser = new SD4ComponentTestingParser();
  protected SD4ComponentTestingCoCoChecker checker;
  protected SD4ComponentTestingTool tool;

  public AbstractCoCoTest() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    this.checker = new SD4ComponentTestingCoCoChecker();
    this.tool = new SD4ComponentTestingTool();
    this.tool.initSymbolTable(new File(SYMBOL_PATH));

    initCoCoChecker();

    Log.getFindings().clear();
  }

  protected abstract void initCoCoChecker();
  protected abstract List<String> getErrorCodeOfCocoUnderTest();

  @ParameterizedTest
  @CsvSource({
    "MainTest.sd4c",
  })
  public void testCorrectExamples(String model) {
    ASTSD4Artifact sd = loadModel(CORRECT_PATH + model);
    checker.checkAll(sd);
    String msgs = Log.getFindings().stream().map(Finding::getMsg).collect(Collectors.joining(System.lineSeparator()));
    Assertions.assertEquals(0, Log.getErrorCount(), msgs);
    Assertions.assertEquals(0,
      Log.getFindings()
        .stream()
        .map(Finding::buildMsg)
        .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
        .count(),
      msgs);
  }

  protected void testCocoViolation(String modelName, int errorCount, int logFindingsCount) {
    ASTSD4Artifact sd = loadModel(INCORRECT_PATH + modelName);
    checker.checkAll(sd);
    Assertions.assertEquals(errorCount, Log.getErrorCount());
    Assertions.assertEquals(logFindingsCount,
      Log.getFindings()
        .stream()
        .map(Finding::buildMsg)
        .filter(f -> getErrorCodeOfCocoUnderTest().stream().anyMatch(f::contains))
        .count());
  }

  public ASTSD4Artifact loadModel(String modelPath) {
    try {
      ASTSD4Artifact ast = parser.parse(modelPath).orElseThrow(NoSuchElementException::new);
      this.tool.createSymbolTableFromAST(ast);
      return ast;
    } catch (IOException | NoSuchElementException e) {
      fail("Loading model: " + modelPath + " failed: " + e.getMessage());
    }
    //can never happen when fail works correct
    throw new IllegalStateException("Something went wrong..");
  }
}
