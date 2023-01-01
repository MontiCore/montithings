// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.languages;

import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Copies handwritten code from its given directory to a "hwc" folder in the target directory
 * JSON and FTL files are ignored in the process
 */
public class CopyLanguagesToTarget extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    try {
      if (state.getConfig().getTargetPlatform() == TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(state.getLanguagePath(),
          Paths.get(state.getTarget().getAbsolutePath()).toFile());
      }
      else {
        FileUtils.copyDirectory(new File(state.getLanguagePath().toString() + "/hierarchy/Sink/target"),
          Paths.get(state.getTarget().getAbsolutePath(), "languages").toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}
