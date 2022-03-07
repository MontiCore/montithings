// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import montithings.CLIState;
import montithings.CLIStep;
import montithings.generator.config.MontiThingsConfiguration;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static montithings.CLIUtils.getDirectoryOrDefault;
import static montithings.MTCLI.DEFAULT_HWC_PATH;

public class GetConfig extends CLIStep {

  @Override public void action(CLIState state) {
    File hwcPath = getDirectoryOrDefault(state.getCmd(), "hwc", Paths.get(DEFAULT_HWC_PATH));
    List<String> hwc = new ArrayList<>();
    hwc.add(hwcPath.getAbsolutePath());
    Map<String, Iterable<String>> params = new HashMap<>();
    params.put("handwrittenCode", hwc);
    params.put("main", Arrays.stream(state.getCmd().getOptionValues("main"))
      .collect(Collectors.toList()));
    addCmdParameter(state.getCmd(), params, "pf", "platform");
    addCmdParameter(state.getCmd(), params, "sp", "splitting");
    addCmdParameter(state.getCmd(), params, "br", "messageBroker");

    Configuration cfg = new ConfigurationPropertiesMapContributor(params);
    state.setMtcfg(MontiThingsConfiguration.withConfiguration(cfg));
  }

  /**
   * Adds a CLI argument to a list of params that can be used by the Groovy Configuration Script
   *
   * @param cmd             command line instance
   * @param params          map of parameters expected by ConfigurationPropertiesMapContributor
   * @param cmdParamName    name of the flag in the CLI
   * @param configParamName name of the parameter in the groovy configuration
   */
  protected void addCmdParameter(CommandLine cmd, Map<String, Iterable<String>> params,
    String cmdParamName, String configParamName) {
    if (cmd.hasOption(cmdParamName)) {
      params.put(configParamName, Arrays.stream(cmd.getOptionValues(cmdParamName))
        .map(String::toUpperCase)
        .collect(Collectors.toList()));
    }
  }

}
