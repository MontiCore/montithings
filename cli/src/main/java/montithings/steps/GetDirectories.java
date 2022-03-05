// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.monticore.io.paths.ModelPath;
import montithings.CLIState;
import montithings.CLIStep;
import org.apache.commons.cli.CommandLine;

import java.nio.file.Paths;

import static montithings.CLIUtils.getDirectoryOrDefault;
import static montithings.MTCLI.*;

public class GetDirectories extends CLIStep {

  @Override public void action(CLIState state) {
    CommandLine cmd = state.getCmd();

    state.setModelPath(
      new ModelPath(getDirectoryOrDefault(cmd, "mp", Paths.get(DEFAULT_MODEL_PATH)).toPath()));
    state.setTestPath(getDirectoryOrDefault(cmd, "tp", Paths.get(DEFAULT_TEST_PATH)));
    state.setHwcPath(getDirectoryOrDefault(cmd, "hwc", Paths.get(DEFAULT_HWC_PATH)));
    state.setTargetDirectory(getDirectoryOrDefault(cmd, "t", Paths.get(DEFAULT_TARGET_PATH)));
  }

}
