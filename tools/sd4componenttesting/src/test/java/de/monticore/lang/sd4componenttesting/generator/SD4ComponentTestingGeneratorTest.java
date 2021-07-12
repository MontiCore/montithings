package de.monticore.lang.sd4componenttesting.generator;

import de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import de.monticore.lang.sd4componenttesting._ast.ASTTestDiagram;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SD4ComponentTestingGeneratorTest {
  protected static final String SYMBOL_PATH = "src/test/resources";
  protected final static String CORRECT_PATH = SYMBOL_PATH + "/examples/correct/";
  protected final static String INCORRECT_PATH = SYMBOL_PATH + "/examples/incorrect/";

  @Test
  void generate() {
    SD4ComponentTestingTool tool = new SD4ComponentTestingTool();
    ASTSD4Artifact ast = tool.loadModel(CORRECT_PATH, "MainTest.sd4c");

    Path path = SD4ComponentTestingGenerator.generate(ast);
  }
}
