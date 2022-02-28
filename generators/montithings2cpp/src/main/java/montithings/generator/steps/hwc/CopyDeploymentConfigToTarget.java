// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.hwc;

import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Copies the deployment configuration file to the target folder
 */
public class CopyDeploymentConfigToTarget extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    File deploymentConfig = new File(
      state.getHwcPath() + File.separator + "deployment-config.json");
    try {
      if (deploymentConfig.exists()) {
        FileUtils.copyFileToDirectory(deploymentConfig, state.getTarget());
      }
      else {
        File emptyConfig = new File(state.getTarget() + File.separator + "deployment-config.json");
        FileUtils.touch(emptyConfig);
        FileUtils.write(emptyConfig, "{}");
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}
