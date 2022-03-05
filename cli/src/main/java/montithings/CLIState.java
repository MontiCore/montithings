// (c) https://github.com/MontiCore/monticore
package montithings;

import de.monticore.io.paths.ModelPath;
import montiarc._ast.ASTMACompilationUnit;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.config.MontiThingsConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.List;

public class CLIState {

  Options options = CLIOptions.initOptions();

  MontiThingsGeneratorTool tool = new MontiThingsGeneratorTool();

  CommandLine cmd;

  List<ASTMACompilationUnit> inputModels;

  ModelPath modelPath;

  File testPath;

  File hwcPath;

  File targetDirectory;

  MontiThingsConfiguration mtcfg;

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Options getOptions() {
    return options;
  }

  public MontiThingsGeneratorTool getTool() {
    return tool;
  }

  public CommandLine getCmd() {
    return cmd;
  }

  public void setCmd(CommandLine cmd) {
    this.cmd = cmd;
  }

  public List<ASTMACompilationUnit> getInputModels() {
    return inputModels;
  }

  public void setInputModels(List<ASTMACompilationUnit> inputModels) {
    this.inputModels = inputModels;
  }

  public ModelPath getModelPath() {
    return modelPath;
  }

  public void setModelPath(ModelPath modelPath) {
    this.modelPath = modelPath;
  }

  public File getTestPath() {
    return testPath;
  }

  public void setTestPath(File testPath) {
    this.testPath = testPath;
  }

  public File getHwcPath() {
    return hwcPath;
  }

  public void setHwcPath(File hwcPath) {
    this.hwcPath = hwcPath;
  }

  public File getTargetDirectory() {
    return targetDirectory;
  }

  public void setTargetDirectory(File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }

  public MontiThingsConfiguration getMtcfg() {
    return mtcfg;
  }

  public void setMtcfg(MontiThingsConfiguration mtcfg) {
    this.mtcfg = mtcfg;
  }

}
