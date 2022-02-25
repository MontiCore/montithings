package de.monticore.lang.sd4componenttesting._visitor;

import de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SD4ComponentTestingFullPrettyPrinterTest {
  protected static final String SYMBOL_PATH = "src/test/resources";
  protected final static String CORRECT_PATH = SYMBOL_PATH + "/examples/correct/";
  protected final static String INCORRECT_PATH = SYMBOL_PATH + "/examples/incorrect/";

  @Test
  void testPPViolation() throws IOException {
    SD4ComponentTestingTool tool = new SD4ComponentTestingTool();
    ASTSD4Artifact ast = tool.loadModel(CORRECT_PATH, CORRECT_PATH + "MainTest.sd4c");

    SD4ComponentTestingFullPrettyPrinter pp = new SD4ComponentTestingFullPrettyPrinter();
    String ppAST = pp.prettyprint(ast);

    String content = new String ( Files.readAllBytes( Paths.get(CORRECT_PATH + "/" + "MainTest.sd4c") ) );


    assertEquals(content, ppAST);
  }
}
