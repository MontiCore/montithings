// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import montithings.CLIState;
import montithings.CLIStep;

public class CoCoCheckConfig extends CLIStep {

  @Override public void action(CLIState state) {
    state.getTool().setStopAfterCoCoCheck(true);
  }

}
