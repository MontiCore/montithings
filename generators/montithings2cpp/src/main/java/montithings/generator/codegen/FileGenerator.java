// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import montithings.generator.helper.FileHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileGenerator {

  /**
   * true if member variables have been set correctly
   */
  protected boolean isInitialized;

  /**
   * Directory in which the generated sources should be placed
   */
  protected File generatedSourcesDirectory;

  /**
   * Directory in which the handwritten code is stored
   */
  protected File hwcDirectory;

  public void generate(File target, String name, String fileExtension, String template,
    Object... templateArguments) {
    Preconditions.checkArgument(isInitialized, "FileGenerator was not initialized");
    Preconditions.checkArgument(FileHelper.fileIsInDirectory(target, generatedSourcesDirectory));

    String packageName = getTargetPathWithoutGeneratedSources(target);
    Boolean existsHwc = existsHWC(hwcDirectory, packageName + name + fileExtension);
    if (existsHwc) {
      name += "TOP";
    }

    Path path = Paths.get(target.getAbsolutePath() + File.separator + name + fileExtension);
    Log.debug("Writing to file " + path, "FileGenerator");


    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    List<Object> templateArgsWithExtra = new ArrayList<>(Arrays.asList(templateArguments));
    templateArgsWithExtra.add(existsHwc);
    engine.generateNoA(template, path, templateArgsWithExtra.toArray());
  }

  protected static boolean existsHWC(File hwcDirectory, String targetFilePath) {
    Path hwcFilePath = Paths.get(hwcDirectory.getAbsolutePath() + File.separator + targetFilePath);
    File hwcFile = hwcFilePath.toFile();
    return hwcFile.exists() && hwcFile.isFile();
  }

  protected String getTargetPathWithoutGeneratedSources(File target) {
    String targetPath = target.getAbsolutePath();
    String result = targetPath.replace(generatedSourcesDirectory.getAbsolutePath(), "");
    if (result.startsWith(File.separator)) {
      result = result.substring(1);
    }
    if (!result.endsWith(File.separator)) {
      result += File.separator;
    }
    return result;
  }

  public FileGenerator(File generatedSourcesDirectory, File hwcDirectory) {
    Preconditions.checkArgument(generatedSourcesDirectory.isDirectory()
      || !generatedSourcesDirectory.exists());
    Preconditions.checkArgument(hwcDirectory.isDirectory());

    this.generatedSourcesDirectory = generatedSourcesDirectory;
    this.hwcDirectory = hwcDirectory;
    this.isInitialized = true;
  }
}
