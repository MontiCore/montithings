// (c) https://github.com/MontiCore/monticore
package network.generator;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import network._ast.ASTNet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generates a python file that generates a MIDI file
 * Overall generation process:
 * Tab File -> MontiCore -> Python File -> MIDI file
 *
 * @author (last commit) kirchhof
 * @version 0.1, 18.03.2019
 * @since 0.1
 */
public class NetworkPythonGenerator {
  /**
   * Generate a python file for generating a MIDI file
   *
   * @param ast the ASTTab to create the music from
   */
  public static Path generate(ASTNet ast) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);

    GeneratorEngine engine = new GeneratorEngine(setup);

    // Generate Python file
    File outputDir = new File(System.getProperty("user.dir") + File.separator + "target");
    Path pythonOutputFile = Paths.get(outputDir.getAbsolutePath(), "Net.py");
    engine.generate("templates/NetworkGenerator.ftl", pythonOutputFile, ast);

    return pythonOutputFile;
  }
}
