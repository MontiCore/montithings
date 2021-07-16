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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MTCLI {
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
        Log.init();
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

      // we need the global scope for symbols and cocos
      ModelPath modelPath = new ModelPath(Paths.get(System.getProperty("user.dir")));
      if (cmd.hasOption("mp")) {
        modelPath = new ModelPath(Arrays.stream(cmd.getOptionValues("mp"))
          .map(Paths::get)
          .collect(Collectors.toList())
        );
      }

      File testPath = getDirectoryOrWorkingDirectory(cmd, "tp");
      File hwcPath = getDirectoryOrWorkingDirectory(cmd, "hwc");
      File targetDirectory = getDirectoryOrWorkingDirectory(cmd, "t");

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
      MontiThingsConfiguration mtcfg = MontiThingsConfiguration.withConfiguration(cfg);

      unpackAllResources(targetDirectory);
      tool.generate(modelPath.getFullPathOfEntries().stream().findFirst().get().toFile(),
        targetDirectory, hwcPath, testPath, mtcfg.configParams);

    }
    catch (ParseException e) {
      // ann unexpected error from the apache CLI parser:
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
  }

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

  public static File getDirectoryOrWorkingDirectory(CommandLine cmd, String paramName) {
    File result = Paths.get(System.getProperty("user.dir")).toFile();
    if (cmd.hasOption(paramName)) {
      result = Arrays.stream(cmd.getOptionValues(paramName))
        .findFirst()
        .map(Paths::get)
        .map(Path::toFile)
        .get();
    }
    return result;
  }

  /**
   * Copy all resources (i.e. RTE and other code) from the JAR to the target directory
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
   * @param srcName folder within the JAR
   * @param targetDirectory directory to which the directory shall be copied
   * @param targetName a subfolder of the target directory, i.e. the new name of the copied folder
   */
  public void unpackResources(String srcName, File targetDirectory, String targetName) {
    // TODO: Does not work outside of Intellij
    try {
      FileUtils.copyDirectory(
        new File(getClass().getResource(srcName).toURI().getPath()),
        new File(targetDirectory.toPath() + File.separator + targetName));
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
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

    // help dialog
    Option help = new Option("h", "Prints this help dialog");
    help.setLongOpt("help");
    options.addOption(help);

    // developer level logging
    Option dev = new Option("d",
      "Specifies whether developer level logging should be used (default is false)");
    dev.setLongOpt("dev");
    options.addOption(dev);

    // parse input file
    Option parse = Option.builder("i")
      .longOpt("input")
      .argName("files")
      .hasArgs()
      .desc("Processes the list of MontiThings input artifacts. " +
        "Argument list is space separated. CoCos are not checked automatically (see -c).")
      .build();
    options.addOption(parse);

    // model paths
    Option path = new Option("p", "Sets the artifact path for imported symbols. "
      + "Directory will be searched recursively for files with the ending "
      + "\".*sym\" (for example \".cdsym\" or \".sym\"). Defaults to the current folder.");
    path.setLongOpt("path");
    path.setArgName("directory");
    path.setOptionalArg(true);
    path.setArgs(1);
    options.addOption(path);

    Option modelPath = new Option("mp", "Sets the model path for the project. "
      + "Directory will be searched recursively for files with the ending "
      + "\".*mt\". Defaults to the current folder.");
    modelPath.setLongOpt("modelpath");
    modelPath.setArgName("directory");
    modelPath.setOptionalArg(true);
    modelPath.setArgs(1);
    options.addOption(modelPath);

    Option hwcPath = new Option("hwc", "Sets the path containing the handwritten "
      + "code. Defaults to the current folder.");
    hwcPath.setLongOpt("handcodedPath");
    hwcPath.setArgName("directory");
    hwcPath.setOptionalArg(true);
    hwcPath.setArgs(1);
    options.addOption(hwcPath);

    Option targetPath = new Option("t", "Set the directory in which to place the"
      + "generated code. Defaults to the current folder + target/generated-sources.");
    targetPath.setLongOpt("target");
    targetPath.setArgName("directory");
    targetPath.setOptionalArg(true);
    targetPath.setArgs(1);
    options.addOption(targetPath);

    Option testPath = new Option("tp", "Sets the path containing the test case "
      + "code. Defaults to the current folder.");
    testPath.setLongOpt("testPath");
    testPath.setArgName("directory");
    testPath.setOptionalArg(true);
    testPath.setArgs(1);
    options.addOption(testPath);

    Option mainComp = new Option("main", "Specifies the fully qualified name of the"
      + "main, i.e., outermost, component.");
    mainComp.setLongOpt("mainComp");
    mainComp.setArgName("directory");
    mainComp.setOptionalArg(false);
    mainComp.setArgs(1);
    options.addOption(mainComp);

    Option platform = Option.builder("pf").
      longOpt("platform").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the platform for which to generate code. Possible arguments are:\n"
        + "-pf generic to generate for generic Linux / Windows / Mac systems,\n"
        + "-pf dsa to generate for DSA VCG,\n"
        + "-pf raspi to generate for Raspberry Pi.")
      .build();
    options.addOption(platform);

    Option splitting = Option.builder("sp").
      longOpt("splitting").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the splitting mode of the generator. Possible arguments are:\n"
        + "-sp off to generate a single binary containing all components,\n"
        + "-sp local to generate one binary per component (for execution on the same device),\n"
        + "-sp distributed to generate one binary per component (for execution on multiple devices)")
      .build();
    options.addOption(splitting);

    Option broker = Option.builder("b").
      longOpt("messageBroker").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the message broker to be used by the architecture. Possible arguments are:\n"
        + "-sp off to use a proprietary one,\n"
        + "-sp mqtt to use Message Queuing Telemetry Transport (Mosquitto MQTT),\n"
        + "-sp dds to Data Distribution Service (OpenDDS)")
      .build();
    options.addOption(broker);

    // pretty print MontiThings
    Option prettyprint = new Option("pp",
      "Prints the OCL model to stdout or the specified file(s) (optional). "
        + "Multiple files should be separated by spaces and will be used in the same order "
        + "in which the input files (-i option) are provided.");
    prettyprint.setLongOpt("prettyprint");
    prettyprint.setArgName("files");
    prettyprint.setOptionalArg(true);
    prettyprint.setArgs(Option.UNLIMITED_VALUES);
    options.addOption(prettyprint);

    // check CoCos
    Option cocos = Option.builder("c").
      longOpt("coco").
      optionalArg(true).
      desc("Checks the CoCos for the input.")
      .build();
    options.addOption(cocos);

    return options;
  }

}
