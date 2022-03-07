// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import montithings.CLIState;
import montithings.CLIStep;
import org.apache.commons.cli.HelpFormatter;

public class Help extends CLIStep {

  @Override public void action(CLIState state) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("MTCLI", state.getOptions());
  }

}
