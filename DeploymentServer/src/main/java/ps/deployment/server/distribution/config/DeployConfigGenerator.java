package ps.deployment.server.distribution.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ps.deployment.server.data.DeploymentInfo;
import ps.deployment.server.data.InstanceInfo;

public class DeployConfigGenerator {
  
  private final DeploymentInfo deployment;
  
  public DeployConfigGenerator(DeploymentInfo deployment) {
    this.deployment = deployment;
  }
  
  public JsonObject generateConfig() {
    JsonObject jsonBase = new JsonObject();
    
    JsonObject jsonDistribution = new JsonObject();
    // Create a description for every executable instance.
    for (InstanceInfo instance : deployment.getInstances()) {
      JsonObject jsonComp = new JsonObject();
      
      // Add all hardware requirements.
      JsonArray jsonSelection = new JsonArray();
      for (String req : instance.getRequirements()) {
        jsonSelection.add(createHasHardware(req));
      }
      
      jsonComp.add("distribution_selection", jsonSelection);
      jsonComp.add("distribution_constraints", new JsonArray()); // TODO add distribution_constraints
      jsonDistribution.add(instance.getComponentType(), jsonComp);
    }
    
    jsonBase.add("distribution", jsonDistribution);
    jsonBase.add("dependencies", new JsonArray()); // TODO add dependencies
    jsonBase.add("incompatibilities", new JsonArray()); // TODO add incompatibilities
    
    return jsonBase;
  }
  
  private JsonElement createHasHardware(String hardware) {
    JsonArray json = new JsonArray(3);
    json.add("has_hardware");
    json.add(hardware);
    json.add(1);
    return json;
  }
  
}
