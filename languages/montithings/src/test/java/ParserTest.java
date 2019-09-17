/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

import de.monticore.symboltable.Symbol;
import montithings._parser.MontiThingsParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTCompilationUnit;


/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class ParserTest {
  public static final boolean ENABLE_FAIL_QUICK = true;

  private static final String MODEL_PATH = "src/test/resources/models";

  private static List<String> expectedParseErrorModels = Collections.singletonList(
          MODEL_PATH + "/portTest/PortTest.mt")
          .stream().map(s -> Paths.get(s).toString())
          .collect(Collectors.toList());

  @BeforeClass
  public static void setUp() {
    // ensure an empty log
    Log.getFindings().clear();
    Log.enableFailQuick(ENABLE_FAIL_QUICK);
  }

  @Test
  public void testArc() throws RecognitionException, IOException {
    test("mt");
  }


  private void test(String fileEnding) throws IOException {
    ParseTest parserTest = new ParseTest("." + fileEnding);
    Files.walkFileTree(Paths.get(MODEL_PATH), parserTest);

    if (!parserTest.getModelsInError().isEmpty()) {
      Log.debug("Models in error", "ParserTest");
      for (String model : parserTest.getModelsInError()) {
        Log.debug("  " + model, "ParserTest");
      }
    }
    Log.info("Count of tested models: " + parserTest.getTestCount(), "ParserTest");
    Log.info("Count of correctly parsed models: "
            + (parserTest.getTestCount() - parserTest.getModelsInError().size()), "ParserTest");

    assertTrue("There were models that could not be parsed", parserTest.getModelsInError()
            .isEmpty());
  }

  /**
   * Visits files of the given file ending and checks whether they are parsable.
   *
   * @author Robert Heim
   * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
   */
  private static class ParseTest extends SimpleFileVisitor<Path> {

    private String fileEnding;

    private List<String> modelsInError = new ArrayList<>();

    private int testCount = 0;

    public ParseTest(String fileEnding) {
      super();
      this.fileEnding = fileEnding;
    }

    /**
     * @return testCount
     */
    public int getTestCount() {
      return this.testCount;
    }

    /**
     * @return modelsInError
     */
    public List<String> getModelsInError() {
      return this.modelsInError;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
      if (file.toFile().isFile()
              && (file.toString().toLowerCase().endsWith(fileEnding))) {

        Log.debug("Parsing file " + file.toString(), "ParserTest");
        testCount++;
        Optional<ASTMTCompilationUnit> maModel = Optional.empty();
        boolean expectingError = ParserTest.expectedParseErrorModels.contains(file.toString());

        MontiThingsParser parser = new MontiThingsParser();
        try {
          if (expectingError) {
            Log.enableFailQuick(false);
          }
          maModel = parser.parse(file.toString());
          System.out.println("test");

        }
        catch (Exception e) {
          if (!expectingError) {
            Log.error("Exception during test", e);
          }
        }
        if (!expectingError && (parser.hasErrors() || !maModel.isPresent())) {
          modelsInError.add(file.toString());
          Log.error("There were unexpected parser errors");
        }
        else {
          Log.getFindings().clear();
        }
        Log.enableFailQuick(ParserTest.ENABLE_FAIL_QUICK);
      }
      return FileVisitResult.CONTINUE;
    }
  }

  ;

}

