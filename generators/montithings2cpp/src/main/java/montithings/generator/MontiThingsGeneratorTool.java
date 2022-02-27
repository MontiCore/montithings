// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.Names;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.config.ConfigParams;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.*;
import montithings.generator.steps.check.*;
import montithings.generator.steps.generate.*;
import montithings.generator.steps.hwc.CopyDeploymentConfigToTarget;
import montithings.generator.steps.hwc.CopyHwcToTarget;
import montithings.generator.steps.hwc.FindCodeTemplates;
import montithings.generator.steps.symbolTable.*;
import montithings.generator.steps.trafos.SetupPortNamesTrafo;
import montithings.generator.steps.trafos.SetupReplayerTrafos;
import montithings.generator.steps.trafos.SetupStatechartTrafos;

import java.io.File;

public class MontiThingsGeneratorTool extends MontiThingsTool {

  public static final String TOOL_NAME = "MontiThingsGeneratorTool";

  protected boolean stopAfterCoCoCheck = false;

  public void generate(File modelPath, File target, File hwcPath, File testPath,
    ConfigParams config) {

    GeneratorToolState state = new GeneratorToolState(this, modelPath, target, hwcPath, testPath,
      config);

    GeneratorStep firstStep = new FindModels();
    firstStep.setNextStep(new SetupGenerator())
      .setNextStep(new CopyHwcToTarget())
      .setNextStep(new CopyDeploymentConfigToTarget())
      .setNextStep(new SetupModelPath())
      .setNextStep(new FindModels())
      .setNextStep(new LogStep("Initializing symboltable", TOOL_NAME))
      .setNextStep(new SerializeCDs())
      .setNextStep(new SetupReplayerTrafos())
      .setNextStep(new SetupStatechartTrafos())
      .setNextStep(new SetupCD4C())
      .setNextStep(new SetupPortNamesTrafo())
      .setNextStep(new SetupMontiThings())
      .setNextStep(new CreateCoComponents())
      .setNextStep(new SetupCDLangExtension())
      .setNextStep(new SetupBindings())
      .setNextStep(new SetupMTCFG())
      .setNextStep(new FindCodeTemplates())
      .setNextStep(new LogStep("Checking models", TOOL_NAME))
      .setNextStep(new ResetMTMill())
      .setNextStep(new CoCoCheck())
      .setNextStep(new CheckIfMainComponentExists())
      .setNextStep(new CheckCdExtensionModels())
      .setNextStep(new CheckBindings())
      .setNextStep(new CheckMTConfig())
      .setNextStep(new ConditionalStop(stopAfterCoCoCheck))
      .setNextStep(new GenerateSensorActuatorPorts())
      .setNextStep(new ClearTemplatesIfReplay())
      .setNextStep(new FindExecutableComponents())
      .setNextStep(new FindModelPacks())
      .setNextStep(new GenerateCMakeLists())
      .setNextStep(new GenerateComponent())
      .setNextStep(new GenerateCDEAdapter())
      .setNextStep(new GenerateCD())
      .setNextStep(new GenerateBuildScripts())
      .setNextStep(new GenerateDeployInfo())
      .setNextStep(new GenerateTestSources())
      .setNextStep(new StopStep())
    ;

    firstStep.execute(state);

  }

  public ComponentTypeSymbol modelToSymbol(String model, IMontiThingsScope symTab) {
    String qualifiedModelName = Names.getQualifier(model)
      + (Names.getQualifier(model).isEmpty() ? "" : ".") + Names.getSimpleName(model);
    return symTab.resolveComponentType(qualifiedModelName).get();
  }

  public boolean isStopAfterCoCoCheck() {
    return stopAfterCoCoCheck;
  }

  public void setStopAfterCoCoCheck(boolean stopAfterCoCoCheck) {
    this.stopAfterCoCoCheck = stopAfterCoCoCheck;
  }
}
