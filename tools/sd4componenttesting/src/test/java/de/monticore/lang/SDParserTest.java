/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang;

import de.monticore.lang.sd4componenttesting._ast.ASTSDArtifact;
import de.monticore.lang.sd4componenttesting._parser.SD4ComponentTestingParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SDParserTest {

  private final static String CORRECT_PATH = "src/test/resources/examples/correct/";
  protected final static String INCORRECT_PATH = "src/test/resources/examples/incorrect/";

  private SD4ComponentTestingParser parser;

  @BeforeEach
  void setup() {
    this.parser = new SD4ComponentTestingParser();
    Log.enableFailQuick(false);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
    System.setErr(new PrintStream(err));
  }

  @ParameterizedTest
  @CsvSource({
    "ConnectionTest.sd",
    "ConnectionTestShort.sd",
    "EmptyTestDiagram.sd",
    "ExpressionsTest.sd",
    "FullTest.sd",
    "InputTest.sd",
    "OutputTest.sd",
    "SingleConnectionStringValueTest.sd",
    "SingleConnectionNumberValueTest.sd",
  })
  void testCorrectExamples(String model) {
    testParseModel(CORRECT_PATH, model);
  }

  @ParameterizedTest
  @CsvSource({

  })
  void testInCorrectExamples(String model) {
    testParseModel(INCORRECT_PATH, model);
  }

  private void testParseModel(String path, String model) {
    try {
      Optional<ASTSDArtifact> ast = parser.parse(path + model);
      assertTrue(ast.isPresent(), "Failed to parse model: " + model);
    } catch (IOException | NoSuchElementException e) {
      System.err.println("Loading model: " + model + " failed: " + e.getMessage());
      fail();
    }
  }
}
