// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.data.Models;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for file-related actions of the generator
 */
public class FileHelper {

  public static void copyHwcToTarget(File target, File hwcPath, String fqComponentName,
    ConfigParams config, Models models, GeneratorToolState state) {
    Set<File> hwcFiles = new HashSet<>();
    try {
      // Get all files in HWC folder
      hwcFiles.addAll(Files.walk(hwcPath.toPath()).map(Path::toFile).collect(Collectors.toSet()));

      // remove the ones that implement components
      // Find all components including those that have no file because they were added by trafos
      Set<ComponentTypeSymbol> components = new HashSet<>();
      for (IMontiThingsScope s : state.getSymTab().getSubScopes()) {
        components.addAll(s.getComponentTypeSymbols().values());
      }

      for (ComponentTypeSymbol comp : components) {
        hwcFiles.removeAll(getHwcClasses(hwcPath, comp.getFullName()));
      }

      // re-add those related to the current component
      hwcFiles.addAll(getHwcClasses(hwcPath, fqComponentName));

      // Remove directories (already covered by their contents) and Freemarker files
      hwcFiles.removeIf(File::isDirectory);
      hwcFiles.removeIf(f -> f.getName().toLowerCase().endsWith(".ftl"));

      // Remove independent ports
      hwcFiles.removeIf(f -> f.getName().startsWith("&"));

      // Now, we're left with the classes for the current component and files not
      // related to any component
    }
    catch (IOException e) {
      e.printStackTrace();
    }



    for (File file : hwcFiles) {
      try {
        if (config.getTargetPlatform() == TargetPlatform.ARDUINO) {
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
    if (config.getSplittingMode() != SplittingMode.OFF) {
      try {
        hwcFiles.addAll(getNonImplFiles(hwcPath));
      }
      catch (IOException e) {
        e.printStackTrace();
        Log.error("Could not get adapter files from HWC.");
      }
    }
  }

  public static void copyTestToTarget(File testPath, File target) {
    try {
      Path targetTestDir = Paths.get(Paths.get(target.getAbsolutePath()).getParent().toString(),
        "generated-test-sources", "sd4c", "cpp");
      FileUtils.copyDirectory(testPath, targetTestDir.toFile());
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public static void makeExecutable(File targetPath, String name, String fileExtension) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + name + fileExtension);
    if (!path.toFile().setExecutable(true)) {
      Log.warn("Could not make '" + path + "' executable");
    }
  }

  public static void copyGeneratedToTarget(File target) {
    try {
      Path targetDir = Paths.get(Paths.get(target.getAbsolutePath()).getParent().toString(),"generated-test-sources");
      if(!targetDir.toAbsolutePath().toString().equals(target.getAbsolutePath())) {
        FileUtils.copyDirectory(Paths.get(target.getAbsolutePath()).toFile(), targetDir.toFile());
        Set<Path> executables;
        try (Stream<Path> walk = Files.walk(target.toPath().getParent())) {
          executables = walk.filter(Files::isRegularFile)
            .filter(Files::isExecutable)
            .filter(f -> !f.toAbsolutePath().toString().toLowerCase().endsWith(".jar"))
            .collect(Collectors.toSet());
        }
        for (Path executable : executables) {
          Path relative = executable.subpath(target.toPath().getNameCount(),executable.getNameCount());
          Path execPath = Paths.get(targetDir.toAbsolutePath().toString(), relative.toString());
          if (!execPath.toFile().setExecutable(true)) {
            Log.warn("Could not make '" + execPath + "' executable");
          }
        }
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
   * @param hwcPath path of handwritten code
   * @return Returns true if a handwritten implementation for the component exist
   */
  public static Boolean existsHWCClass(File hwcPath, String fqComponentName) {
    Set<File> hwcFilesForComponent = getHwcClasses(hwcPath, fqComponentName);
    return hwcFilesForComponent.stream().anyMatch(f -> f.toPath().toString().toLowerCase().endsWith("impl.cpp")) &&
      hwcFilesForComponent.stream().anyMatch(f -> f.toPath().toString().toLowerCase().endsWith("impl.h"));
  }

  public static Set<File> getHwcClasses(File hwcPath, String fqComponentName) {
    String compName = Names.getSimpleName(fqComponentName);
    String packageName = fqComponentName.substring(0, fqComponentName.lastIndexOf(".") + 1);
    String compFilePrefix = packageName.replace(".", File.separator);

    String regex = Pattern.quote(compFilePrefix) + "(Deploy)?" + Pattern.quote(compName)
      + "(Impl|Input|Result|Precondition[0-9]*|Postcondition[0-9]*|Interface|State)?\\.(cpp|h)";
    Pattern pattern = Pattern.compile(regex);

    Set<File> result = new HashSet<>();

    try {
      result = Files.walk(hwcPath.toPath())
        .filter(p -> p != hwcPath.toPath())
        .map(p -> p.toString().substring(hwcPath.toPath().toString().length() + 1))
        .filter(pattern.asPredicate())
        .map(p -> Paths.get(hwcPath.toPath() + File.separator + p).toFile())
        .collect(Collectors.toSet());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static Set<File> getPortImplementation(File hwcPath, String fqComponentName) {
    Set<File> result = new HashSet<>();
    Set<String> fileEndings = getFileEndings();

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

  public static Set<String> getFilesWithEnding(File hwcPath, Set<String> fileEndings) {
    return getFilesWithEnding(hwcPath, fileEndings, true);
  }

  public static Set<String> getFilesWithEnding(File hwcPath, Set<String> fileEndings, boolean recursive) {
    Set<String> result = new HashSet<>();

    if (hwcPath.isDirectory()) {
      for (String ending : fileEndings) {
        String[] files = new String[0];
        try {
          Stream<Path> pathStream;
          if (recursive) {
            pathStream = Files.walk(hwcPath.toPath());
          } else {
            pathStream = Arrays.stream(Objects.requireNonNull(hwcPath.listFiles())).map(File::toPath);
          }

          files = pathStream
            .filter(name -> name.toString()
            .endsWith(ending))
            .map(path -> path.getFileName().toString().split(ending)[0])
            .toArray(String[]::new);
        } catch (IOException e) {
          e.printStackTrace();
        }
        Collections.addAll(result, files);
      }
    }
    return result;
  }

  public static Set<String> getFilesWithPrefix(File hwcPath, Set<String> filePrefix) {
    return getFilesWithPrefix(hwcPath, filePrefix, true);
  }

  public static Set<String> getFilesWithPrefix(File hwcPath, Set<String> filePrefix, boolean recursive) {
    Set<String> result = new HashSet<>();

    if (hwcPath.isDirectory()) {
      for (String prefix : filePrefix) {
        String[] files = new String[0];
        try {
          Stream<Path> pathStream;

          if (recursive) {
            pathStream = Files.walk(hwcPath.toPath());
          } else {
            pathStream = Arrays.stream(Objects.requireNonNull(hwcPath.listFiles())).map(File::toPath);
          }

          files = pathStream
            .map(path -> path.getFileName().toString())
            .filter(name -> name.startsWith(prefix))
            .toArray(String[]::new);
        } catch (IOException e) {
          e.printStackTrace();
        }
        Collections.addAll(result, files);
      }
    }
    return result;
  }

  public static Set<String> getFileEndings() {
    Set<String> fileEndings = new HashSet<>();
    fileEndings.add("Include.ftl");
    fileEndings.add("Body.ftl");
    fileEndings.add("Provide.ftl");
    fileEndings.add("Consume.ftl");
    fileEndings.add("Init.ftl");
    fileEndings.add("Topic.ftl");
    fileEndings.add("Type.ftl");
    return fileEndings;
  }

  public static String getFullPackageName(File directory, File hwcDirectory) {
    String result = directory.toString().substring(hwcDirectory.toString().length());
    if (result.startsWith(File.separator)) {
      result = result.substring(1);
    }
    result = result.replace(File.separator, ".");
    return result;
  }

  /**
   * Returns list of all subpackages paths
   *
   * @param modelPath location of models on the file system
   * @return list of all subpackages paths
   */
  public static File[] getSubPackagesPath(String modelPath) {
    ArrayList<File> subPackagesPaths = new ArrayList<>();
    File[] subDirs = new File(modelPath).listFiles(File::isDirectory);

    // Iterate over subdirectories of the model folder and add the paths of the subdirs to array
    for (File subDir : Objects.requireNonNull(subDirs)) {
      subPackagesPaths.add(new File(subDir.getAbsolutePath()));
    }

    // cast to ArrayList to File[] and return
    return subPackagesPaths.toArray(new File[subPackagesPaths.size()]);
  }

  public static boolean fileIsInDirectory(File file, File directory) {
    return file.getAbsolutePath().startsWith(directory.getAbsolutePath());
  }
}
