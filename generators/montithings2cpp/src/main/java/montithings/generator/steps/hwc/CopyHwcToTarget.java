// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.hwc;

import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.io.FileUtils;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Copies handwritten code from its given directory to a "hwc" folder in the target directory
 * JSON and FTL files are ignored in the process
 */
public class CopyHwcToTarget extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    try {
      FileFilter filefilter = pathname -> !pathname.getName().endsWith(".ftl")
        && !pathname.getName().endsWith(".json");
      if (state.getConfig().getTargetPlatform() == TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(state.getHwcPath(),
          Paths.get(state.getTarget().getAbsolutePath()).toFile(), filefilter);
      }
      else {
        FileUtils.copyDirectory(state.getHwcPath(),
          Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile(), filefilter);
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}
