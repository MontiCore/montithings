// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Utility class for file-related actions of the generator
 *
 * @since 27.07.20
 */
public class FileHelper {

  public static void copyHwcToTarget(File target, File hwcPath, ConfigParams config) {
    try {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath()).toFile());
      }
      else {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath(), "hwc").toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Returns list of all subpackages paths
   *
   * @param modelPath
   * @return
   */
  public static File[] getSubPackagesPath(String modelPath) {
    ArrayList<File> subPackagesPaths = new ArrayList<>();
    File[] subDirs = new File(modelPath).listFiles(File::isDirectory);

    // Iterate over subdirectories of the model folder and add the paths of the subdirs to array
    for (File subDir : subDirs) {
      subPackagesPaths.add(new File(subDir.getAbsolutePath()));
    }

    // cast to ArrayList to File[] and return
    return subPackagesPaths.toArray(new File[subPackagesPaths.size()]);
  }
}
