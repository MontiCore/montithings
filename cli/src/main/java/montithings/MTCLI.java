// (c) https://github.com/MontiCore/monticore
package montithings;

import montithings.steps.*;

public class MTCLI {

  public static final String DEFAULT_MODEL_PATH = "src/main/resources/models";

  public static final String DEFAULT_HWC_PATH = "src/main/resources/hwc";

  public static final String DEFAULT_LANGUAGE_PATH = "src/main/resources/languages";

  public static final String DEFAULT_TEST_PATH = "src/test/resources/gtests";

  public static final String DEFAULT_TARGET_PATH = "target/generated-sources";

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
    try {
      CLIStep firstStep = new ParseArguments(args);
      firstStep.setNextStep(new Help().onlyIf(CLIOptions.help()))
        .setNextStep(new Help().onlyIf(CLIOptions.noOption()))
        .setNextStep(new Stop().onlyIf(CLIOptions.help()))
        .setNextStep(new Stop().onlyIf(CLIOptions.noOption()))
        .setNextStep(new InitLogging())
        .setNextStep(new ParseInputModels().onlyIf(CLIOptions.input()))
        .setNextStep(new PrettyPrint().onlyIf(CLIOptions.input()).onlyIf(CLIOptions.prettyprint()))
        .setNextStep(new Stop().onlyIf(CLIOptions.input()))
        .setNextStep(new Stop().onlyIf(CLIOptions.prettyprint()))
        .setNextStep(new EnsureMainIsPresent())
        .setNextStep(new CoCoCheckConfig().onlyIf(CLIOptions.stopAfterCoCoChecks()))
        .setNextStep(new GetDirectories())
        .setNextStep(new GetConfig())
        .setNextStep(new UnpackResources())
        .setNextStep(new Generate())
      ;

      CLIState state = new CLIState();
      firstStep.execute(state);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}
