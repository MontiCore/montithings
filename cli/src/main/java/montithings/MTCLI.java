// (c) https://github.com/MontiCore/monticore
package montithings;

import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.MontiThingsConfiguration;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

public class MTCLI {

  public static final String DEFAULT_MODEL_PATH = "src/main/resources/models";

  public static final String DEFAULT_HWC_PATH = "src/main/resources/hwc";

  public static final String DEFAULT_TEST_PATH = "src/test/resources/gtests";

  public static final String DEFAULT_TARGET_PATH = "target/generated-sources";

  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/

  /**
   * Main method that is called from command line and runs the OCL tool.
   *
   * @param args The input parameters for configuring the OCL tool.
   */
  public static void main(String[] args) {
    MTCLI cli = new MTCLI();
    cli.run(args);
  }

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param args The input parameters for configuring the OCL tool.
   */
  public void run(String[] args) {

    Options options = initOptions();
    MontiThingsGeneratorTool tool = new MontiThingsGeneratorTool();

    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      // help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        // do not continue, when help is printed
        return;
      }

      // -option developer logging
      if (cmd.hasOption("d")) {
        Log.initDEBUG();
      }
      else {
        CLILogger.init();
      }

      // parse input file, which is now available
      // (only returns if successful)
      if (cmd.hasOption("i")) {
        List<ASTMACompilationUnit> inputModels = new ArrayList<>();
        for (String inputFileName : cmd.getOptionValues("i")) {
          Optional<ASTMACompilationUnit> ast = tool.parse(inputFileName);
          if (ast.isPresent()) {
            inputModels.add(ast.get());
          }
          else {
            Log.error("0xMTCLI0100 File '" + inputFileName + "' cannot be parsed");
          }
        }

        // -option pretty print
        if (cmd.hasOption("pp")) {
          int ppArgs = cmd.getOptionValues("pp") == null ? 0 : cmd.getOptionValues("pp").length;
          int iArgs = cmd.getOptionValues("i") == null ? 0 : cmd.getOptionValues("i").length;
          if (ppArgs != 0 && ppArgs != iArgs) {
            Log.error("0xMTCLI0101 Number of arguments of -pp (which is " + ppArgs
              + ") must match number of arguments of -i (which is " + iArgs + "). "
              + "Or provide no arguments to print to stdout.");
          }

          String[] paths = cmd.getOptionValues("pp");
          int i = 0;
          for (ASTMACompilationUnit compUnit : inputModels) {
            String currentPath = "";
            if (cmd.getOptionValues("pp") != null && cmd.getOptionValues("pp").length != 0) {
              currentPath = paths[i];
              i++;
            }
            prettyprint(compUnit, currentPath);
          }
        }
        return;
      }

      if (!cmd.hasOption("main")) {
        Log.error("0xMTCLI0104 Parameter 'main' not present but required for everything "
          + "other than CoCo checks.");
      }

      // Coco check configuration
      if (cmd.hasOption("c")) {
        tool.setStopAfterCoCoCheck(true);
      }

      // Extract paths from command line
      ModelPath modelPath = new ModelPath(
        getDirectoryOrDefault(cmd, "mp", Paths.get(DEFAULT_MODEL_PATH)).toPath());
      File testPath = getDirectoryOrDefault(cmd, "tp", Paths.get(DEFAULT_TEST_PATH));
      File hwcPath = getDirectoryOrDefault(cmd, "hwc", Paths.get(DEFAULT_HWC_PATH));
      File targetDirectory = getDirectoryOrDefault(cmd, "t", Paths.get(DEFAULT_TARGET_PATH));
      MontiThingsConfiguration mtcfg = getMontiThingsConfigurationFromCliParams(cmd);

      // Unpack RTE from JAR
      unpackAllResources(targetDirectory);

      // Execute generator
      tool.generate(modelPath.getFullPathOfEntries().stream().findFirst().get().toFile(),
        targetDirectory, hwcPath, testPath, mtcfg.configParams);

    }
    catch (ParseException e) {
      // ann unexpected error from the apache CLI parser:
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Extract a MontiThingsConfiguration from the command line arguments
   *
   * @param cmd the command line instance
   * @return a MontiThingsConfiguration containing the generator configuration
   */
  protected MontiThingsConfiguration getMontiThingsConfigurationFromCliParams(CommandLine cmd) {
    File hwcPath = getDirectoryOrDefault(cmd, "hwc", Paths.get(DEFAULT_HWC_PATH));
    List<String> hwc = new ArrayList<>();
    hwc.add(hwcPath.getAbsolutePath());
    Map<String, Iterable<String>> params = new HashMap<>();
    params.put("handwrittenCode", hwc);
    params.put("main", Arrays.stream(cmd.getOptionValues("main"))
      .collect(Collectors.toList()));
    addCmdParameter(cmd, params, "pf", "platform");
    addCmdParameter(cmd, params, "sp", "splitting");
    addCmdParameter(cmd, params, "br", "messageBroker");

    Configuration cfg = new ConfigurationPropertiesMapContributor(params);
    return MontiThingsConfiguration.withConfiguration(cfg);
  }

  /**
   * Adds a CLI argument to a list of params that can be used by the Groovy Configuration Script
   *
   * @param cmd             command line instance
   * @param params          map of parameters expected by ConfigurationPropertiesMapContributor
   * @param cmdParamName    name of the flag in the CLI
   * @param configParamName name of the parameter in the groovy configuration
   */
  protected void addCmdParameter(CommandLine cmd, Map<String, Iterable<String>> params,
    String cmdParamName, String configParamName) {
    if (cmd.hasOption(cmdParamName)) {
      params.put(configParamName, Arrays.stream(cmd.getOptionValues(cmdParamName))
        .map(String::toUpperCase)
        .collect(Collectors.toList()));
    }
  }

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param options The input parameters and options.
   */
  public void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("MTCLI", options);
  }

  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/

  /**
   * Prints the contents of the MT-AST to stdout or a specified file.
   *
   * @param compilationUnit The MT-AST to be pretty printed
   * @param file            The target file name for printing the OCL artifact. If empty,
   *                        the content is printed to stdout instead
   */
  public void prettyprint(ASTMACompilationUnit compilationUnit, String file) {
    // pretty print AST
    MontiThingsFullPrettyPrinter pp = new MontiThingsFullPrettyPrinter(new IndentPrinter());
    String OCL = pp.prettyprint(compilationUnit);
    print(OCL, file);
  }

  /**
   * Prints the given content to a target file (if specified) or to stdout (if
   * the file is Optional.empty()).
   *
   * @param content The String to be printed
   * @param path    The target path to the file for printing the content. If empty,
   *                the content is printed to stdout instead
   */
  public void print(String content, String path) {
    // print to stdout or file
    if (path == null || path.isEmpty()) {
      System.out.println(content);
    }
    else {
      File f = new File(path);
      // create directories (logs error otherwise)
      f.getAbsoluteFile().getParentFile().mkdirs();

      FileWriter writer;
      try {
        writer = new FileWriter(f);
        writer.write(content);
        writer.close();
      }
      catch (IOException e) {
        Log.error("0xA7105 Could not write to file " + f.getAbsolutePath());
      }
    }
  }

  /**
   * Return the path given by the paramName argument via CLI, or the current working directory
   * if the parameter is not set in the CLI
   *
   * @param cmd         the command line instance
   * @param paramName   name of the command line parameter
   * @param defaultPath path to be used if cmd parameter does not exist
   * @return the path in the argument (if present), the current working directory (otherwise)
   */
  public static File getDirectoryOrDefault(CommandLine cmd, String paramName, Path defaultPath) {
    File result = defaultPath.toFile();
    if (cmd.hasOption(paramName)) {
      result = Arrays.stream(cmd.getOptionValues(paramName))
        .findFirst()
        .map(Paths::get)
        .map(Path::toFile)
        .get();
      checkPathExists(result, paramName);
    }
    return result;
  }

  /**
   * Copy all resources (i.e. RTE and other code) from the JAR to the target directory
   *
   * @param targetDirectory the directory to place the generated code in
   */
  protected void unpackAllResources(File targetDirectory) {
    unpackResources("/rte/montithings-RTE", targetDirectory, "montithings-RTE");
    unpackResources("/header", targetDirectory, "header");
    unpackResources("/lib", targetDirectory, "lib");
    unpackResources("/python", targetDirectory, "python");
    File testTargetDirectory = Paths.get(
      Paths.get(targetDirectory.getAbsolutePath()).getParent().toString(),
      "generated-test-sources").toFile();
    unpackResources("/test", testTargetDirectory, "test");
  }

  /**
   * Copy a single directory from the JAR to the destination
   *
   * @param srcName         folder within the JAR
   * @param targetDirectory directory to which the directory shall be copied
   * @param targetName      a subfolder of the target directory, i.e. the new name of the copied folder
   */
  public void unpackResources(String srcName, File targetDirectory, String targetName) {
    try {
      copyFromJar(srcName, Paths.get(targetDirectory.toPath() + File.separator + targetName));
    }
    catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * Copy folder from current JAR.
   * Adapted from https://stackoverflow.com/a/24316335
   *
   * @param source path within JAR
   * @param target copy destination directory
   */
  public void copyFromJar(String source, final Path target) throws URISyntaxException, IOException {
    URI resource = Objects.requireNonNull(MTCLI.class.getResource("")).toURI();
    FileSystem fileSystem;
    try {
      fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap());
    }
    catch (FileSystemAlreadyExistsException e) {
      fileSystem = FileSystems.getFileSystem(resource);
    }

    final Path jarPath = fileSystem.getPath(source);

    Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
        Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
        Files.createDirectories(currentTarget);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, target.resolve(jarPath.relativize(file).toString()),
          StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }

    });
  }

  /**
   * Checks that a given path exists and points to a directory.
   * Calls Log.error if path does not refer to a valid directory.
   *
   * @param file      the path to check
   * @param paramName name of the parameter with which this path was given to the CLI
   */
  protected static void checkPathExists(File file, String paramName) {
    if (!file.exists()) {
      try {
        Files.createDirectories(file.toPath());
      }
      catch (IOException e) {
        Log.error("0xMTCLI0102 Could not create directory '" + file.toPath() +
          "' for parameter '" + paramName + "'.");
        e.printStackTrace();
      }
    }
  }

  /*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

  /**
   * Initializes the available CLI options for the MontiThings tool.
   *
   * @return The CLI options with arguments.
   */
  protected Options initOptions() {
    Options options = new Options();

    ///
    /// GENERAL FLAGS
    ///

    // help dialog
    Option help = new Option("h", "Prints this help dialog");
    help.setLongOpt("help");
    options.addOption(help);

    // developer level logging
    Option dev = new Option("d",
      "Specifies whether developer level logging should be used (default is false)");
    dev.setLongOpt("dev");
    options.addOption(dev);

    // pretty print
    options.addOption(Option.builder("pp")
      .desc("Prints the OCL model to stdout or the specified file(s) (optional). "
        + "Multiple files should be separated by spaces and will be used in the same order "
        + "in which the input files (-i option) are provided.")
      .longOpt("prettyprint")
      .argName("files")
      .optionalArg(true)
      .numberOfArgs(Option.UNLIMITED_VALUES)
      .build()
    );

    // check cocos only - no generation
    options.addOption(Option.builder("c").
      longOpt("coco").
      optionalArg(true).
      desc("Checks the CoCos for the input.")
      .build()
    );

    ///
    /// PATHS
    ///

    // parse single input file
    options.addOption(Option.builder("i")
      .longOpt("input")
      .argName("files")
      .hasArgs()
      .desc("Processes the list of MontiThings input artifacts. " +
        "Argument list is space separated. CoCos are not checked automatically (see -c).")
      .build()
    );

    // model path
    options.addOption(Option.builder("mp")
      .longOpt("modelpath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the model path for the project. "
        + "Directory will be searched recursively for files with the ending "
        + "\".*mt\". Defaults to the current folder + '" + DEFAULT_MODEL_PATH + "'.")
      .build()
    );

    // handwritten code
    options.addOption(Option.builder("hwc")
      .longOpt("handcodedPath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the path containing the handwritten code. "
        + "Defaults to the current folder + '" + DEFAULT_HWC_PATH + "'.")
      .build()
    );

    // target
    options.addOption(Option.builder("t")
      .longOpt("target")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Set the directory in which to place the "
        + "generated code. Defaults to the current folder + '" + DEFAULT_TARGET_PATH + "'.")
      .build()
    );

    // test cases
    options.addOption(Option.builder("tp")
      .longOpt("testPath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the path containing the test case code. "
        + "Defaults to the current folder + '" + DEFAULT_TEST_PATH + "'.")
      .build()
    );

    ///
    /// GENERATOR ARGUMENTS
    ///

    // main component
    options.addOption(Option.builder("main")
      .longOpt("mainComp")
      .argName("directory")
      .optionalArg(false)
      .numberOfArgs(1)
      .desc("Specifies the fully qualified name of the main, i.e., outermost, component.")
      .build()
    );

    // platform
    options.addOption(Option.builder("pf").
      longOpt("platform").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the platform for which to generate code. Possible arguments are:\n"
        + "-pf generic to generate for generic Linux / Windows / Mac systems,\n"
        + "-pf dsa to generate for DSA VCG,\n"
        + "-pf raspi to generate for Raspberry Pi.")
      .build()
    );

    // splitting mode
    options.addOption(Option.builder("sp")
      .longOpt("splitting")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Set the splitting mode of the generator. Possible arguments are:\n"
        + "-sp off to generate a single binary containing all components,\n"
        + "-sp local to generate one binary per component (for execution on the same device),\n"
        + "-sp distributed to generate one binary per component (for execution on multiple devices)")
      .build()
    );

    // message broker
    options.addOption(Option.builder("b").
      longOpt("messageBroker").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the message broker to be used by the architecture. Possible arguments are:\n"
        + "-b off to use a proprietary one,\n"
        + "-b mqtt to use Message Queuing Telemetry Transport (Mosquitto MQTT),\n"
        + "-b dds to Data Distribution Service (OpenDDS)")
      .build()
    );

    return options;
  }

}
