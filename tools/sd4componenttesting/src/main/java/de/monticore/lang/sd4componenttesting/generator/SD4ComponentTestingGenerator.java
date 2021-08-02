package de.monticore.lang.sd4componenttesting.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;

/**
 * Generates a c++ file that contains test cases
 * Overall generation process:
 * Arc File + SD$C File -> MontiCore -> C++ File -> Test Cases
 *
 * @author (last commit) bockhorst
 * @version 0.1, 28.06.2021
 * @since 0.1
 */

public class SD4ComponentTestingGenerator {
  /**
   * Generate a C++ file for Test Cases
   *
   * @param ast the ASTSD4Artifact to create the Test Cases from
   */

  public static Path generate(ASTSD4Artifact ast) {
    return generate(ast, "target/Test.cpp");
  }

  public static Path generate(ASTSD4Artifact ast, String path ) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);

    GeneratorEngine engine = new GeneratorEngine(setup);

    // Generate CPP file
    File outputDir = new File(System.getProperty("user.dir"));
    Path TestCasesOutputFile = Paths.get(outputDir.getAbsolutePath(), path);
    engine.generate("templates/TestCasesGenerator.ftl", TestCasesOutputFile, ast);

    return TestCasesOutputFile;
  }
}
