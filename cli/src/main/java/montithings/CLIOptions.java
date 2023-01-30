// (c) https://github.com/MontiCore/monticore
package montithings;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import static montithings.MTCLI.*;

public class CLIOptions {

  /**
   * Initializes the available CLI options for the MontiThings tool.
   *
   * @return The CLI options with arguments.
   */
  protected static Options initOptions() {
    Options options = new Options();
    options.addOption(help());
    options.addOption(verbose());
    options.addOption(prettyprint());
    // check cocos only - no generation
    options.addOption(stopAfterCoCoChecks());

    options.addOption(input());
    options.addOption(modelPath());
    options.addOption(hwc());
    options.addOption(languagePath());
    options.addOption(target());
    options.addOption(testPath());

    options.addOption(mainComponent());
    options.addOption(platform());
    options.addOption(splitting());
    options.addOption(messageBroker());

    return options;
  }

  public static Option noOption() {
    return new Option("", "");
  }

  //============================================================================
  // region General Flags
  //============================================================================

  public static Option help() {
    Option help = new Option("h", "Prints this help dialog");
    help.setLongOpt("help");
    return help;
  }

  public static Option verbose() {
    Option dev = new Option("d",
      "Specifies whether developer level logging should be used (default is false)");
    dev.setLongOpt("dev");
    return dev;
  }

  public static Option prettyprint() {
    return Option.builder("pp")
      .desc("Prints the OCL model to stdout or the specified file(s) (optional). "
        + "Multiple files should be separated by spaces and will be used in the same order "
        + "in which the input files (-i option) are provided.")
      .longOpt("prettyprint")
      .argName("files")
      .optionalArg(true)
      .numberOfArgs(Option.UNLIMITED_VALUES)
      .build();
  }

  public static Option stopAfterCoCoChecks() {
    return Option.builder("c").
      longOpt("coco").
      optionalArg(true).
      desc("Checks the CoCos for the input.")
      .build();
  }

  // endregion
  //============================================================================
  // region Paths
  //============================================================================

  public static Option input() {
    return Option.builder("i")
      .longOpt("input")
      .argName("files")
      .hasArgs()
      .desc("Processes the list of MontiThings input artifacts. " +
        "Argument list is space separated. CoCos are not checked automatically (see -c).")
      .build();
  }

  public static Option modelPath() {
    return Option.builder("mp")
      .longOpt("modelpath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the model path for the project. "
        + "Directory will be searched recursively for files with the ending "
        + "\".*mt\". Defaults to the current folder + '" + DEFAULT_MODEL_PATH + "'.")
      .build();
  }

  public static Option hwc() {
    return Option.builder("hwc")
      .longOpt("handcodedPath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the path containing the handwritten code. "
        + "Defaults to the current folder + '" + DEFAULT_HWC_PATH + "'.")
      .build();
  }

  public static Option languagePath() {
    return Option.builder("lp")
      .longOpt("languagepath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the lamguage path for the project. "
        + "Defaults to the current folder + '" + DEFAULT_LANGUAGE_PATH + "'.")
      .build();
  }

  public static Option target() {
    return Option.builder("t")
      .longOpt("target")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Set the directory in which to place the "
        + "generated code. Defaults to the current folder + '" + DEFAULT_TARGET_PATH + "'.")
      .build();
  }

  public static Option testPath() {
    return Option.builder("tp")
      .longOpt("testPath")
      .argName("directory")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Sets the path containing the test case code. "
        + "Defaults to the current folder + '" + DEFAULT_TEST_PATH + "'.")
      .build();
  }

  // endregion
  //============================================================================
  // region Generator Arguments
  //============================================================================

  public static Option mainComponent() {
    return Option.builder("main")
      .longOpt("mainComp")
      .argName("directory")
      .optionalArg(false)
      .numberOfArgs(1)
      .desc("Specifies the fully qualified name of the main, i.e., outermost, component.")
      .build();
  }

  public static Option platform() {
    return Option.builder("pf").
      longOpt("platform").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the platform for which to generate code. Possible arguments are:\n"
        + "-pf generic to generate for generic Linux / Windows / Mac systems,\n"
        + "-pf dsa to generate for DSA VCG,\n"
        + "-pf raspi to generate for Raspberry Pi.")
      .build();
  }

  public static Option splitting() {
    return Option.builder("sp")
      .longOpt("splitting")
      .optionalArg(true)
      .numberOfArgs(1)
      .desc("Set the splitting mode of the generator. Possible arguments are:\n"
        + "-sp off to generate a single binary containing all components,\n"
        + "-sp local to generate one binary per component (for execution on the same device),\n"
        + "-sp distributed to generate one binary per component (for execution on multiple devices)")
      .build();
  }

  public static Option messageBroker() {
    return Option.builder("b").
      longOpt("messageBroker").
      optionalArg(true).
      numberOfArgs(1).
      desc("Set the message broker to be used by the architecture. Possible arguments are:\n"
        + "-b off to use a proprietary one,\n"
        + "-b mqtt to use Message Queuing Telemetry Transport (Mosquitto MQTT),\n"
        + "-b dds to Data Distribution Service (OpenDDS)")
      .build();
  }

  // endregion
}
