// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;
import montithings.services.iot_manager.server.data.constraint.DependencyConstraint;
import montithings.services.iot_manager.server.data.constraint.IncompConstraint;
import montithings.services.iot_manager.server.data.constraint.LocationConstraint;
import montithings.services.iot_manager.server.exception.DeploymentException;
import montithings.services.iot_manager.server.util.ThrowingFunction;

public class DeploymentConfiguration {
  
  private DeploymentInfo deploymentInfo;
  private List<Constraint> constraints;
  
  public DeploymentInfo getDeploymentInfo() {
    return deploymentInfo;
  }
  
  public void setDeploymentInfo(DeploymentInfo deploymentInfo) {
    this.deploymentInfo = deploymentInfo;
  }
  
  public List<Constraint> getConstraints() {
    return constraints;
  }
  
  public void setConstraints(List<Constraint> constraints) {
    this.constraints = constraints;
  }
  
  public JsonObject getConstraintsAsJson() throws DeploymentException {
    JsonObject json = new JsonObject();
    JsonArray jBasic = new JsonArray();
    json.add("basicConstraints", jBasic);
    
    JsonArray jDep = new JsonArray();
    json.add("dependencyConstraints", jDep);
    
    JsonArray jIncomp = new JsonArray();
    json.add("incompConstraints", jIncomp);
    
    for(Constraint con : this.constraints) {
      if(con instanceof BasicConstraint) {
        jBasic.add(con.serializeJson());        
      } else if(con instanceof DependencyConstraint) {
        jDep.add(con.serializeJson());
      } else if(con instanceof IncompConstraint) {
        jIncomp.add(con.serializeJson());
      }
    }
    
    return json;
  }
  
  public static DeploymentConfiguration fromJson(String jsonConfigStr) throws DeploymentException {
    return fromJson(JsonParser.parseString(jsonConfigStr).getAsJsonObject());
  }
  
  public static DeploymentConfiguration fromJson(JsonObject jsonConfig) throws DeploymentException {
    JsonObject jsonInfo = jsonConfig.getAsJsonObject("deploymentInfo");
    
    // rename "deployComponentInstance" to "instances" for easy parsing. 
    JsonElement jInstances = jsonInfo.get("deployComponentInstance");
    jsonInfo.remove("deployComponentInstance");
    jsonInfo.add("instances", jInstances);
    
    DeploymentInfo info = DeploymentInfo.fromJson(jsonInfo);
    List<Constraint> constraints = new ArrayList<>();
    
    // declare parsers for constraint types
    Map<String, ThrowingFunction<JsonObject, Constraint, DeploymentException>> constraintParsers = new HashMap<>();
    constraintParsers.put("deploymentConstraint", BasicConstraint::fromJson);
    constraintParsers.put("deploymentDependencyConstraint", DependencyConstraint::fromJson);
    constraintParsers.put("deploymentLocationConstraint", LocationConstraint::fromJson);
    constraintParsers.put("deploymentIncompConstraint", IncompConstraint::fromJson);
    
    // parse constraints for each constraint type
    for(Entry<String, ThrowingFunction<JsonObject, Constraint, DeploymentException>> e : constraintParsers.entrySet()) {
      // find array for this constraint type
      JsonElement jeConstraints = jsonConfig.get(e.getKey());
      if(jeConstraints != null && jeConstraints.isJsonArray()) {
        JsonArray jConstraints = jeConstraints.getAsJsonArray();
        
        for(JsonElement jeConsraint : jConstraints) {
          JsonObject jConstraint = jeConsraint.getAsJsonObject();
          // parse constraint by corresponding parser
          Constraint constraint = e.getValue().apply(jConstraint);
          constraints.add(constraint);
        }
      }
    }
    
    DeploymentConfiguration conf = new DeploymentConfiguration();
    conf.setDeploymentInfo(info);
    conf.setConstraints(constraints);
    return conf;
  }
  
  @Override
  public DeploymentConfiguration clone() {
    DeploymentConfiguration cloned = new DeploymentConfiguration();
    cloned.setConstraints(new ArrayList<>(this.constraints));
    cloned.setDeploymentInfo(this.deploymentInfo);
    return cloned;
  }
  
  @Override
  public String toString() {
    return "DeploymentConfiguration [deploymentInfo=" + deploymentInfo + ", constraints=" + constraints + "]";
  }
  
}
