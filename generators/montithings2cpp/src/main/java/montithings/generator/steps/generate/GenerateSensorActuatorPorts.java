// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;
import static montithings.generator.helper.FileHelper.*;

public class GenerateSensorActuatorPorts extends GeneratorStep {

  @Override
  public void action(GeneratorToolState state) {
    File[] packages = state.getHwcPath().listFiles();

    try {
      packages = Files.walk(Paths.get(state.getHwcPath().getAbsolutePath()))
          .filter(Files::isDirectory)
          .map(Path::toFile)
          .distinct()
          .toArray(File[]::new);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<String> executableSensorActuatorPorts = new ArrayList<>();

    for (File pckg : Objects.requireNonNull(packages)) {
      Set<String> sensorActuatorPorts = getFilesWithEnding(pckg, getFileEndings(), false);
      for (String port : sensorActuatorPorts) {
        if (!templatePortBelongsToComponent(port, state)) {
          Log.debug("Processing handwritten port '" + port + "'", TOOL_NAME);
          state.getMtg().generateSensorActuatorPort(port, getFullPackageName(pckg, state.getHwcPath()),
              state.getConfig());
          generateCMakeForSensorActuatorPort(getFullPackageName(pckg, state.getHwcPath()), port, state);
          executableSensorActuatorPorts.add(getFullPackageName(pckg, state.getHwcPath()) + "." + port);
        }
      }
    }

    for (File pckg : Objects.requireNonNull(packages)) {
      Set<String> sensorActuatorPorts = getFilesWithPrefix(pckg, Collections.singleton("&"), false);
      for (String port : sensorActuatorPorts) {
        Log.debug("Processing handwritten port '" + port + "'", TOOL_NAME);

        // Sensorname = Get name without &
        String cleanName = FilenameUtils.removeExtension(port.substring(1));
        String fullName = getFullPackageName(pckg, state.getHwcPath()) + "." + cleanName;

        // Create folder
        File directory = Paths.get(state.getTarget().getPath(), fullName).toFile();
        directory.mkdir();

        // Copy to Target-generated sources to "package + . + Sensorname"
        try {
          Files.copy(Paths.get(pckg.toString(), port), Paths.get(directory.toPath().toString(), port.substring(1)),
              REPLACE_EXISTING);
        } catch (IOException e) {
          e.printStackTrace();
          Log.error("0xMT1201 Could not copy '" + Paths.get(pckg.toString(), port) + "' to '"
              + Paths.get(directory.toPath().toString(), port.substring(1)) + "'");
        }

        // Generate CMake
        generateCMakeForCppPort(getFullPackageName(pckg, state.getHwcPath()), cleanName, state);

        // add to executableSensorActuatorPorts (so it is processed in overall CMake)
        executableSensorActuatorPorts.add(fullName);
      }
    }

    if (!executableSensorActuatorPorts.isEmpty()
        && state.getConfig().getSplittingMode() == SplittingMode.OFF) {
      Log.error("Cannot use SplittingMode OFF with SensorActuatorPorts");
    }

    List<String> hwcPythonScripts = new ArrayList<>();
    for (File pckg : packages) {
      Set<String> pythonScriptsWithoutPckg = getFilesWithEnding(
          new File(state.getHwcPath() + File.separator + pckg.getName()),
          Stream.of(".py").collect(Collectors.toSet()));
      for (String script : pythonScriptsWithoutPckg) {
        hwcPythonScripts.add(pckg.getName() + "." + script);
      }
    }

    List<String> cppRequirements = new ArrayList<>();
    for (File pckg : packages) {
      Set<String> cppRequirementsFiles = getFilesWithEnding(
          new File(state.getHwcPath() + File.separator + pckg.getName()),
          Stream.of(".txt").collect(Collectors.toSet()));
      for (String txtFilename : cppRequirementsFiles) {
        if (txtFilename.equals("cppRequirements")) {
          List<String> fileContent = readTxtLineByLine(new File(
              state.getHwcPath() + File.separator + pckg.getName() + File.separator + txtFilename + ".txt"));
          cppRequirements.addAll(fileContent);
        }
      }
    }

    Log.info("Found " + cppRequirements.size() + " cppRequirements", TOOL_NAME);

    state.setExecutableSensorActuatorPorts(executableSensorActuatorPorts);
    state.setHwcPythonScripts(hwcPythonScripts);
    state.setCppRequirements(cppRequirements);
  }

  private List<String> readTxtLineByLine(File file) {
    List<String> lines = new ArrayList<>();
    try {
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
          lines.add(line);
        }
        return lines;
      }
    } catch (IOException e) {
      return lines;
    }
  }

  protected void generateCMakeForSensorActuatorPort(String pckg, String port,
      GeneratorToolState state) {
    if (state.getConfig().getTargetPlatform() != TargetPlatform.ARDUINO) { // Arduino uses its own build system
      Log.info("Generate CMake file for " + port, TOOL_NAME);
      state.getMtg().generateMakeFileForSensorActuatorPort(pckg, port, "montithings-RTE");
    }
  }

  protected void generateCMakeForCppPort(String pckg, String port,
      GeneratorToolState state) {
    if (state.getConfig().getTargetPlatform() != TargetPlatform.ARDUINO) { // Arduino uses its own build system
      Log.info("Generate CMake file for " + port, TOOL_NAME);
      state.getMtg().generateCMakeFileForCppPort(pckg, port, "montithings-RTE");
    }
  }

  public boolean templatePortBelongsToComponent(String portName, GeneratorToolState state) {

    // Get all names of the FTL files for templating a port by file name
    Set<String> sensorActuatorPortNames = new HashSet<>();

    for (PortSymbol port : state.getConfig().getTemplatedPorts()) {
      if (!port.getComponent().isPresent()) {
        Log.error(
            String.format("0xMT1112 Templated port '%s' has no component", port.getFullName()));
      }
      if (state.getConfig().getTemplatedPorts().contains(port)) {
        sensorActuatorPortNames.add(
            StringUtils.capitalize(port.getComponent().get().getName()) +
                StringUtils.capitalize(port.getName()) +
                "Port");
      }
    }

    // check if any of the templated ports matches the given port name
    return sensorActuatorPortNames.stream().anyMatch(portName::startsWith);
  }

}
