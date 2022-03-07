// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import montithings.CLIState;
import montithings.CLIStep;
import montithings.MTCLI;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Objects;

/**
 * Copy all resources (i.e. RTE and other code) from the JAR to the target directory
 */
public class UnpackResources extends CLIStep {

  @Override public void action(CLIState state) {
    unpackResources("/rte/montithings-RTE", state.getTargetDirectory(), "montithings-RTE");
    unpackResources("/header", state.getTargetDirectory(), "header");
    unpackResources("/lib", state.getTargetDirectory(), "lib");
    unpackResources("/python", state.getTargetDirectory(), "python");
    File testTargetDirectory = Paths.get(
      Paths.get(state.getTargetDirectory().getAbsolutePath()).getParent().toString(),
      "generated-test-sources").toFile();
    unpackResources("/test", testTargetDirectory, "test");
  }

  /**
   * Copy a single directory from the JAR to the destination
   *
   * @param srcName         folder within the JAR
   * @param targetDirectory directory to which the directory shall be copied
   * @param targetName      a subfolder of the target directory, i.e. the new name of the copied folder
   */
  public void unpackResources(String srcName, File targetDirectory, String targetName) {
    try {
      copyFromJar(srcName, Paths.get(targetDirectory.toPath() + File.separator + targetName));
    }
    catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * Copy folder from current JAR.
   * Adapted from https://stackoverflow.com/a/24316335
   *
   * @param source path within JAR
   * @param target copy destination directory
   */
  public void copyFromJar(String source, final Path target) throws URISyntaxException, IOException {
    URI resource = Objects.requireNonNull(MTCLI.class.getResource("")).toURI();
    FileSystem fileSystem;
    try {
      fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap());
    }
    catch (FileSystemAlreadyExistsException e) {
      fileSystem = FileSystems.getFileSystem(resource);
    }

    final Path jarPath = fileSystem.getPath(source);

    Files.walkFileTree(jarPath, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
        Path currentTarget = target.resolve(jarPath.relativize(dir).toString());
        Files.createDirectories(currentTarget);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.copy(file, target.resolve(jarPath.relativize(file).toString()),
          StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }

    });
  }

}
