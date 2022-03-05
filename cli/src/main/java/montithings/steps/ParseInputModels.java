// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.CLIState;
import montithings.CLIStep;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static montithings.CLIUtils.checkFileExists;

public class ParseInputModels extends CLIStep {

  @Override public void action(CLIState state) {
    List<ASTMACompilationUnit> inputModels = new ArrayList<>();
    for (String inputFileName : state.getCmd().getOptionValues("i")) {
      checkFileExists(new File(inputFileName));
      Optional<ASTMACompilationUnit> ast = state.getTool().parse(inputFileName);
      if (ast.isPresent()) {
        inputModels.add(ast.get());
      }
      else {
        Log.error("0xMTCLI0100 File '" + inputFileName + "' cannot be parsed");
      }
    }
    state.setInputModels(inputModels);
  }

}
