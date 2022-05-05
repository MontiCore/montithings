// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import montithings.generator.config.SplittingMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.util.*;

public class GeneratePackageFiles extends GeneratorStep {

  @Override
  public void action(GeneratorToolState state) {
    // Skip execution if splitting mode is off. As the Package files are currently not used for anything,
    // this does not influence the correctness of the generated code. Package file generation for other
    // splitting modes can be added later in this method if it becomes necessary.
    if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
      return;
    }

    File[] directories = new File(state.getModelPath().getAbsolutePath()).listFiles(File::isDirectory);

    //get all subpackage paths and their respective components
    Map<String, List<File>> packagePaths = handleDirectories(directories, state.getModelPath().getName() + File.separator, false);

    //generate a package file for each subpackage
    for (String path : packagePaths.keySet()) {
      state.getMtg().generatePackageFile(path, packagePaths.get(path));
    }
  }

  public Map<String, List<File>> handleDirectories(File[] directories, String currentPackage, boolean subPackage) {
    Map<String, List<File>> packages = new HashMap<>();
    for (File dir : directories) {
      //add current package with its components only if it is not base package
      if (subPackage) {
        List<File> models = Arrays.asList(dir.listFiles(((dir1, name) -> name.toLowerCase().endsWith(".mt"))));
        packages.put(currentPackage + dir.getName(), models);
      }

      //add all subpackages of current package recursively
      packages.putAll(handleDirectories(dir.listFiles(File::isDirectory), dir.getName() + File.separator, true));
    }
    return packages;
  }
}
