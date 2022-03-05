// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.se_rwth.commons.logging.Log;
import montithings.CLIState;
import montithings.CLIStep;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class ParseArguments extends CLIStep {

  protected String[] args;

  @Override public void action(CLIState state) {
    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(state.getOptions(), args);
      state.setCmd(cmd);
    }
    catch (ParseException e) {
      // ann unexpected error from the apache CLI parser:
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ParseArguments(String[] args) {
    this.args = args;
  }

}
