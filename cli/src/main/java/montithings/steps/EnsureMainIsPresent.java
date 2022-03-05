// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.se_rwth.commons.logging.Log;
import montithings.CLIState;
import montithings.CLIStep;

public class EnsureMainIsPresent extends CLIStep {

  @Override public void action(CLIState state) {
    if (!state.getCmd().hasOption("main")) {
      Log.error("0xMTCLI0104 Parameter 'main' not present but required for everything "
        + "other than CoCo checks.");
    }
  }

}
