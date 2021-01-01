// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Utility class for file-related actions of the generator
 *
 * @since 27.07.20
 */
public class FileHelper {

  public static void copyHwcToTarget(File target, File hwcPath, ConfigParams config) {
    try {
      FileFilter filefilter = new FileFilter(){
        public boolean accept(File pathname) {
          if (pathname.getName().endsWith(".ftl")) {
            return false;
          } else {
            return true;
          }
        }
      };
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath()).toFile(), (FileFilter) filefilter);
      }
      else {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath(), "hwc").toFile(), filefilter);
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
    addNonImplFiles(hwcPath, config, hwcFiles);

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

  protected static void addNonImplFiles(File hwcPath, ConfigParams config, Set<File> hwcFiles) {
    if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      try {
        hwcFiles.addAll(getNonImplFiles(hwcPath));
      }
      catch (IOException e) {
        e.printStackTrace();
        Log.error("Could not get adapter files from HWC.");
      }
    }
  }

  public static void copyTestToTarget(File testPath, File target, ComponentTypeSymbol comp) {
    String fileName = comp.getFullName().replace('.','_') + "Test.cpp";
    try {
        Path targetTestDir = Paths.get(Paths.get(target.getAbsolutePath()).getParent().toString(),"generated-test-sources","test","gtests");
        Path testFile = Paths.get(testPath.getAbsolutePath(),fileName);
      if(testFile.toFile().isFile()) {
        FileUtils.copyFileToDirectory(testFile.toFile(), targetTestDir.toFile());
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
      if(!targetDir.toAbsolutePath().toString().equals(target.getAbsolutePath().toString())) {
        FileUtils.copyDirectory(Paths.get(target.getAbsolutePath()).toFile(), targetDir.toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public static Set<File> getNonImplFiles(File hwcPath) throws IOException {
    Set<File> allFiles = Files.walk(hwcPath.toPath())
      .filter(Files::isRegularFile)
      .map(Path::toFile)
      .collect(Collectors.toSet());

    return allFiles.stream()
      .filter(f -> !f.getName().toLowerCase().endsWith("impl.cpp"))
      .filter(f -> !f.getName().toLowerCase().endsWith("impl.h"))
      .filter(f -> !f.getName().toLowerCase().endsWith(".ftl"))
      .collect(Collectors.toSet());
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

  public static Set<File> getPortImplementation(File hwcPath, String fqComponentName) {
    Set<File> result = new HashSet<>();
    Set<String> fileEndings = new HashSet<>();
    fileEndings.add("Include.ftl");
    fileEndings.add("Body.ftl");
    fileEndings.add("Provide.ftl");
    fileEndings.add("Consume.ftl");

    for (String ending : fileEndings) {
      File hwcFile = Paths.get(hwcPath.toString() + File.separator
          + fqComponentName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
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

  public static boolean fileIsInDirectory(File file, File directory) {
    return file.getAbsolutePath().startsWith(directory.getAbsolutePath());
  }
}
