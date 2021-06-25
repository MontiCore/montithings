/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
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

  private final static String PATH = "src/test/resources/examples/parser/";

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
    "ConnectionTest.sd4c",
    "EmptyTestDiagram.sd4c",
    "ExpressionsTest.sd4c",
    "FullTest.sd4c",
    "InputTest.sd4c",
    "OutputTest.sd4c",
    "SingleConnectionStringValueTest.sd4c",
    "SingleConnectionNumberValueTest.sd4c",
  })
  void testCorrectExamples(String model) {
    try {
      Optional<ASTSD4Artifact> ast = parser.parse(PATH + model);
      assertTrue(ast.isPresent(), "Failed to parse model: " + model);
    } catch (IOException | NoSuchElementException e) {
      System.err.println("Loading model: " + model + " failed: " + e.getMessage());
      fail();
    }
  }
}
