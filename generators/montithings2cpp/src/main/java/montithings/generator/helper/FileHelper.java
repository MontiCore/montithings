// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

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

  public static void copyHwcToTarget(File target, File hwcPath, String fqComponentName,
    ConfigParams config) {
    Set<File> hwcFiles = getHwcClassWithoutExtension(hwcPath, fqComponentName);
    for (File file : hwcFiles) {
      try {
        if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
          FileUtils.copyFileToDirectory(file, Paths.get(target.getAbsolutePath()).toFile());
        }
        else {
          FileUtils.copyFileToDirectory(file, Paths.get(target.getAbsolutePath(), "hwc").toFile());
        }
      }
      catch (IOException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void copyTestToTarget(File testPath, File target) {
    try {
        Path targetTestDir = Paths.get(Paths.get(target.getAbsolutePath()).getParent().toString(),"generated-test-sources","test");
        Path testDir = Paths.get(testPath.getAbsolutePath(),"resources");
      if(testDir.toFile().isDirectory()) {
        FileUtils.copyDirectory(testDir.toFile(), targetTestDir.toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public static void copyGeneratedToTarget(File target) {
    try {
      Path targetDir = Paths.get(Paths.get(target.getAbsolutePath()).getParent().toString(),"generated-test-sources");
      if(!targetDir.toString().equals(target.toString())) {
        FileUtils.copyDirectory(Paths.get(target.getAbsolutePath()).toFile(), targetDir.toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * @param hwcPath
   * @return Returns true if a handwritten implementation for the component exist
   */
  public static Boolean existsHWCClass(File hwcPath, String fqComponentName) {
    return !getHwcClassWithoutExtension(hwcPath, fqComponentName).isEmpty();
  }

  public static Set<File> getHwcClassWithoutExtension(File hwcPath, String fqComponentName) {
    Set<File> result = new HashSet<>();
    Set<String> fileEndings = new HashSet<>();
    fileEndings.add(".h");
    fileEndings.add(".cpp");

    for (String ending : fileEndings) {
      File hwcFile = Paths.get(hwcPath.toString() + File.separator
        + fqComponentName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + "Impl"
        + ending)
        .toFile();
      if (hwcFile.isFile()) {
        result.add(hwcFile);
      }
    }
    return result;
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
