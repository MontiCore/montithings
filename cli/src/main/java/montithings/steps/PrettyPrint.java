// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.CLIState;
import montithings.CLIStep;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrettyPrint extends CLIStep {

  @Override public void action(CLIState state) {
    validateArgumentCount(state);

    String[] paths = state.getCmd().getOptionValues("pp");
    int i = 0;
    for (ASTMACompilationUnit compUnit : state.getInputModels()) {
      String currentPath = "";
      if (state.getCmd().getOptionValues("pp") != null
        && state.getCmd().getOptionValues("pp").length != 0) {
        currentPath = paths[i];
        i++;
      }
      prettyprint(compUnit, currentPath);
    }
  }

  protected void validateArgumentCount(CLIState state) {
    CommandLine cmd = state.getCmd();
    int ppArgs = cmd.getOptionValues("pp") == null ? 0 : cmd.getOptionValues("pp").length;
    int iArgs = cmd.getOptionValues("i") == null ? 0 : cmd.getOptionValues("i").length;
    if (ppArgs != 0 && ppArgs != iArgs) {
      Log.error("0xMTCLI0101 Number of arguments of -pp (which is " + ppArgs
        + ") must match number of arguments of -i (which is " + iArgs + "). "
        + "Or provide no arguments to print to stdout.");
    }
  }

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

}
