// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.io.FileReaderWriter;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.lang3.tuple.Pair;
import montiarc.util.Modelfinder;
import de.se_rwth.commons.logging.Log;

import javax.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class GenerateDeployInfo extends GeneratorStep {
  public static final String TF_EXTENSION = "tf";

  @Override
  public void action(GeneratorToolState state) {
    JsonObjectBuilder jsonBase = Json.createObjectBuilder();

    // Collect executable instances.
    JsonArrayBuilder jsonInstances = Json.createArrayBuilder();

    for (Pair<ComponentTypeSymbol, String> instance : state.getInstances()) {
      // Each executable instance will be added to the "instances" array.
      ComponentTypeSymbol comp = instance.getKey();
      JsonObjectBuilder jsonInstance = Json.createObjectBuilder();

      jsonInstance.add("componentType", comp.getFullName());
      jsonInstance.add("instanceName", instance.getValue());
      jsonInstance.add("dockerImage", comp.getFullName().toLowerCase() + ":latest");

      // Also add the requirements of the component.
      JsonArrayBuilder jreqs = Json.createArrayBuilder();
      for (String req : ComponentHelper.getRequirements(comp, state.getConfig())) {
        if (req.startsWith("ocl:")) {
          jsonInstance.add("hardwareRequirements", req.substring(4));
        } else {
          jreqs.add(req);
        }
      }
      jsonInstance.add("requirements", jreqs.build());

      JsonArrayBuilder terraformInfo = generateTerraformInfo(state);
      jsonInstance.add("terraformInfo", terraformInfo);

      jsonInstances.add(jsonInstance);
    }
    jsonBase.add("instances", jsonInstances.build());

    // Serialize JSON and write it to a file.
    String jsonString = jsonBase.build().toString();
    File jsonFile = new File(state.getTarget(), "deployment-info.json");
    FileReaderWriter.storeInFile(jsonFile.getAbsoluteFile().toPath(), jsonString);
  }

  private JsonArrayBuilder generateTerraformInfo(GeneratorToolState state) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(state.getModelPath(), TF_EXTENSION);

    Log.info("Generating Terraform Base64 String for " + foundModels.size() + " models...", TOOL_NAME);

    JsonArrayBuilder terraformInfos = Json.createArrayBuilder();

    for (String model : foundModels) {
      Log.info("Generate TF Info: " + model, TOOL_NAME);

      try {
        JsonObjectBuilder terraformInfo = Json.createObjectBuilder();
        String modelName = model + "." + TF_EXTENSION;
        String modelPath = state.getModelPath().getAbsolutePath() + "/" + modelName;
        String encodedTf = fileToBase64Str(modelPath);
        terraformInfo.add("filename", modelName);
        terraformInfo.add("filecontent", encodedTf);
        terraformInfos.add(terraformInfo);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return terraformInfos;
  }

  private String fileToBase64Str(String fileName) throws IOException {
    File file = new File(fileName);
    byte[] fileContent = Files.readAllBytes(file.toPath());
    return Base64.getEncoder().encodeToString(fileContent);
  }

}
