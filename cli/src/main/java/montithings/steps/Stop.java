// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import montithings.CLIState;
import montithings.CLIStep;

public class Stop extends CLIStep {

  @Override public void action(CLIState state) {
    System.exit(0);
  }

}
