// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.InstanceInfo;
import montithings.services.iot_manager.server.data.constraint.Constraint;

public class DeployConfigBuilder {
  
  private final DeploymentConfiguration config;
  private JsonObject jsonBase;
  private JsonArray jsonDependencies, jsonIncompatibilities;
  
  public DeployConfigBuilder(DeploymentConfiguration deployment) {
    this.config = deployment;
    this.applyBaseConfig();
  }
  
  /**
   * Applies the constraints from the base deployment config given in the constructor.
   * */
  public DeployConfigBuilder applyConfigConstraints() {
    for(Constraint c : config.getConstraints()) {
      c.applyConstraint(this);
    }
    return this;
  }
  
  private JsonObject applyBaseConfig() {
    this.jsonBase = new JsonObject();
    
    JsonObject jsonDistribution = new JsonObject();

    // Create a description for every executable instance.
    for (InstanceInfo instance : config.getDeploymentInfo().getInstances()) {
      JsonObject jsonComp = new JsonObject();
      
      // Add all hardware requirements.
      JsonArray jsonSelection = new JsonArray();
      for (String req : instance.getRequirements()) {
        jsonSelection.add(createHasHardware(req));
      }
      
      jsonComp.add("distribution_selection", jsonSelection);
      jsonComp.add("distribution_constraints", new JsonArray());
      jsonDistribution.add(instance.getInstanceName(), jsonComp);
    }
    
    jsonDependencies = new JsonArray();
    jsonIncompatibilities = new JsonArray();
    
    jsonBase.add("distribution", jsonDistribution);
    jsonBase.add("dependencies", jsonDependencies);
    jsonBase.add("incompatibilities", jsonIncompatibilities);
    
    return jsonBase;
  }
  
  private JsonArray getJsonArrayForInstance(String instanceName, String arrayName) {
    JsonObject jDistribution = jsonBase.getAsJsonObject("distribution");
    
    // find object for instance or create one
    JsonElement jeInstance = jDistribution.get(instanceName);
    if (jeInstance == null || jeInstance.isJsonNull()) {
      // This instance does not exist. Thus, we can ignore the constraint and
      // return a dummy JsonArray to prevent null values.
      return new JsonArray();
    }
    JsonObject jInstance = jeInstance.getAsJsonObject();
    
    // find election array for instance or create one
    JsonElement jeDistributionSelection = jInstance.get(arrayName);
    if (jeDistributionSelection == null || jeDistributionSelection.isJsonNull()) {
      jeDistributionSelection = new JsonArray();
      jInstance.add(arrayName, jeDistributionSelection);
    }
    return jeDistributionSelection.getAsJsonArray();
  }
  
  public JsonArray distributionSelectionFor(String instanceName) {
    return this.getJsonArrayForInstance(instanceName, "distribution_selection");
  }
  
  public JsonArray distributionConstraintsFor(String instanceName) {
    return this.getJsonArrayForInstance(instanceName, "distribution_constraints");
  }
  
  public JsonArray dependencies() {
    return this.jsonDependencies;
  }
  
  public JsonArray incompatibilities() {
    return this.jsonIncompatibilities;
  }
  
  public JsonObject build() {
    return this.jsonBase;
  }
  
  private JsonElement createHasHardware(String hardware) {
    JsonArray json = new JsonArray(3);
    json.add("has_hardware");
    json.add(hardware);
    json.add(1);
    return json;
  }
}
