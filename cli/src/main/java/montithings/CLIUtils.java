// (c) https://github.com/MontiCore/monticore
package montithings;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CLIUtils {

  /**
   * Return the path given by the paramName argument via CLI, or the current working directory
   * if the parameter is not set in the CLI
   *
   * @param cmd         the command line instance
   * @param paramName   name of the command line parameter
   * @param defaultPath path to be used if cmd parameter does not exist
   * @return the path in the argument (if present), the current working directory (otherwise)
   */
  public static File getDirectoryOrDefault(CommandLine cmd, String paramName, Path defaultPath) {
    File result = defaultPath.toFile();
    if (cmd.hasOption(paramName)) {
      result = Arrays.stream(cmd.getOptionValues(paramName))
        .findFirst()
        .map(Paths::get)
        .map(Path::toFile)
        .get();
      checkPathExists(result, paramName);
    }
    return result;
  }

  /**
   * Checks that a given path exists and points to a directory.
   * Calls Log.error if path does not refer to a valid directory.
   *
   * @param file      the path to check
   * @param paramName name of the parameter with which this path was given to the CLI
   */
  protected static void checkPathExists(File file, String paramName) {
    if (!file.exists()) {
      try {
        Files.createDirectories(file.toPath());
      }
      catch (IOException e) {
        Log.error("0xMTCLI0102 Could not create directory '" + file.toPath() +
          "' for parameter '" + paramName + "'.");
        e.printStackTrace();
      }
    }
  }

  /**
   * Checks that a given path exists and points to a file.
   * Calls Log.error if path does not refer to a valid file.
   *
   * @param file the path to check
   */
  public static void checkFileExists(File file) {
    if (!file.exists()) {
      Log.error("0xMTCLI0103 Path '" + file.toPath() +
        "' does not exist.");
    }
    if (!file.isFile()) {
      Log.error("0xMTCLI0104 Path '" + file.toPath() +
        "' exists but does not refer to a file.");
    }
  }

}
