// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.io.FileReaderWriter;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.lang3.tuple.Pair;

import javax.json.*;
import java.io.File;

public class GenerateDeployInfo extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
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
        }
        else {
          jreqs.add(req);
        }
      }
      jsonInstance.add("requirements", jreqs.build());

      jsonInstances.add(jsonInstance);
    }
    jsonBase.add("instances", jsonInstances.build());

    // Serialize JSON and write it to a file.
    String jsonString = jsonBase.build().toString();
    File jsonFile = new File(state.getTarget(), "deployment-info.json");
    FileReaderWriter.storeInFile(jsonFile.getAbsoluteFile().toPath(), jsonString);
  }

}
