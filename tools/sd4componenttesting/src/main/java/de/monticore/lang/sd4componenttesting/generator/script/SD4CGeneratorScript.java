// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.generator.script;

import de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.groovy.GroovyInterpreter;
import de.se_rwth.commons.groovy.GroovyRunner;
import de.se_rwth.commons.logging.Log;
import groovy.lang.Script;
import montiarc._ast.ASTMACompilationUnit;
import montithings.generator.config.Options;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SD4CGeneratorScript extends Script implements GroovyRunner {

  protected static final String[] DEFAULT_IMPORTS = {};

  protected static final String LOG = "MontiThingsGeneratorScript";

  /**
   * @see GroovyRunner#run(String,
   * Configuration)
   */
  @Override
  public void run(String script, Configuration configuration) {
    GroovyInterpreter.Builder builder = GroovyInterpreter.newInterpreter()
      .withScriptBaseClass(SD4CGeneratorScript.class)
      .withClassLoader(getClass().getClassLoader())
      .withImportCustomizer(new ImportCustomizer().addStarImports(DEFAULT_IMPORTS));

    // configuration
    SD4CConfiguration config = SD4CConfiguration.withConfiguration(configuration);

    // we add the configuration object as property with a special property name
    builder.addVariable(SD4CConfiguration.CONFIGURATION_PROPERTY, config);

    config.getAllValues().forEach(builder::addVariable);

    // after adding everything we override a couple of known variable
    // bindings to have them properly typed in the script
    builder.addVariable(Options.MODELPATH.toString(), config.getModelPath());
    builder.addVariable(Options.TESTPATH.toString(), config.getTestPath());
    builder.addVariable(Options.OUT.toString(), config.getOut());

    GroovyInterpreter g = builder.build();
    g.evaluate(script);
  }

  /**
   * Gets called by Groovy Script. Generates component artifacts for each
   * component in {@code modelPath} to {@code targetPath}
   */
  public void generate(File modelPath, File testPath, File targetPath) {
    try {
      final String ARC_MODELS_TARGET = targetPath + File.separator + "arc" + File.separator;
      createDirs(ARC_MODELS_TARGET);

      final String CPP_FILES_TARGET = targetPath + File.separator + "cpp" + File.separator;
      createDirs(CPP_FILES_TARGET);

      prettyPrintMtToArc(modelPath.toPath(), ARC_MODELS_TARGET);
      generateCppFromSd4c(testPath, ARC_MODELS_TARGET, CPP_FILES_TARGET, testPath.toPath());

    } catch (java.io.IOException e) {
      Log.error("A Problem occurred while trying to read or write files");
    }
  }


  /**
   * pretty prints a set of .mt Models to .arc Files.
   *
   * @param modelPath         Location of the .mt Models
   * @param ARC_MODELS_TARGET desired target location for .arc files
   * @throws IOException if there is a problem reading the mt models
   */
  private void prettyPrintMtToArc(Path modelPath, String ARC_MODELS_TARGET) throws IOException {
    montithings._parser.MontiThingsParser mtParser = new montithings._parser.MontiThingsParser();
    final montithings._visitor.MontiThingsToMontiArcFullPrettyPrinter printer
      = new montithings._visitor.MontiThingsToMontiArcFullPrettyPrinter();

    Set<Path> mtModels = getAllFilesWithExtension("mt", modelPath);

    for (Path mtModel : mtModels) {
      info("Generating .arc Model for " + mtModel.getFileName());

      String packagePath = mtModel.toString().replace(modelPath.toString() + File.separator, "");
      String arcFilePath = ARC_MODELS_TARGET + packagePath.replace(".mt", ".arc");

      final Optional<ASTMACompilationUnit> ast = mtParser.parse(mtModel.toString());
      String arcFileContent = printer.prettyprint(ast.get());
      writeToNewFile(arcFilePath, arcFileContent);
    }
  }


  /**
   * generates cpp test files from sd4c model files
   *
   * @param testModelsLocation location of the sd4c test models
   * @param ARC_MODELS_TARGET  location of the arc models
   * @param CPP_FILES_TARGET   desired location for the cpp files
   * @throws IOException if there is a problem reading the sd4c models
   */
  private void generateCppFromSd4c(File testModelsLocation, String ARC_MODELS_TARGET,
                                   String CPP_FILES_TARGET, Path testPath) throws IOException {

    SD4ComponentTestingTool tool = new SD4ComponentTestingTool();
    final String CPP_GENERATOR_PATH = CPP_FILES_TARGET.replace(System.getProperty("user.dir"), "");

    if (!testModelsLocation.exists()) {
      Log.error(String.format(SD4ComponentTestingError.TEST_MODELS_LOCATION_NON_EXISTENT.toString(),
        testModelsLocation.getAbsolutePath()));
    }

    Set<Path> sd4cModels = getAllFilesWithExtension("sd4c", testModelsLocation.toPath());

    for (Path sd4cModel : sd4cModels) {
      info("Generating .cpp File for " + sd4cModel.getFileName());

      String packagePath = sd4cModel.toString().replace(testPath.toString() + File.separator, "");

      tool.generate(
        ARC_MODELS_TARGET,
        sd4cModel.toString(),
        CPP_GENERATOR_PATH + packagePath.replace(".sd4c", ".cpp")
      );
    }
  }

  /**
   * creates the directory structure contained in path
   *
   * @param path a String containing a path
   * @throws IOException if there is a problem creating the directory structure
   */
  private void createDirs(String path) throws IOException {
    File dir = new File(path);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new IOException("could not create directories at \"" + dir.getAbsolutePath() + "\"");
    }
  }

  /**
   * looks for all files with a specific file extension in a directory tree
   *
   * @param fileExtension a String containing a file extension such as ".pdf" , "txt" ...
   * @param startPath     the head of the directory tree to search
   * @return a Set containing the Paths to all files with the given file extension
   * @throws IOException if there is a problem reading the file tree
   */
  protected Set<Path> getAllFilesWithExtension(String fileExtension, Path startPath) throws IOException {
    final String finalFileExtension = fileExtension.replace(".", "");
    return Files.walk(startPath)
      .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith("." + finalFileExtension))
      .collect(Collectors.toSet());
  }

  /**
   * creates a new File and writes a String to it
   *
   * @param filePath path to a non-existing file
   * @param content  a String
   * @throws IOException if there is an error while creating the file
   */
  public void writeToNewFile(String filePath, String content) throws IOException {
    File file = new File(filePath);
    createDirs(file.toString().replace(file.getName(), ""));
    file.createNewFile();
    FileWriter writer = new FileWriter(filePath);
    writer.write(content);
    writer.close();
  }

  // #######################
  // log functions
  // #######################

  public boolean isDebugEnabled() {
    return Log.isDebugEnabled(LOG);
  }

  public void debug(String msg) {
    Log.debug(msg, LOG);
  }

  public void debug(String msg, Throwable t) {
    Log.debug(msg, t, LOG);
  }

  public boolean isInfoEnabled() {
    return Log.isInfoEnabled(LOG);
  }

  public void info(String msg) {
    Log.info(msg, LOG);
  }

  public void info(String msg, Throwable t) {
    Log.info(msg, t, LOG);
  }

  public void warn(String msg) {
    Log.warn(msg);
  }

  public void warn(String msg, Throwable t) {
    Log.warn(msg, t);
  }

  public void error(String msg) {
    Log.error(msg);
  }

  public void error(String msg, Throwable t) {
    Log.error(msg, t);
  }

  /**
   * @see Script#run()
   */
  @Override
  public Object run() {
    return true;
  }
}
