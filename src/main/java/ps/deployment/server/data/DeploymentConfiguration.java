package ps.deployment.server.data;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ps.deployment.server.data.constraint.BasicConstraint;
import ps.deployment.server.data.constraint.Constraint;
import ps.deployment.server.exception.DeploymentException;

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
    JsonArray jarr = new JsonArray();
    json.add("entries", jarr);
    
    for(Constraint con : this.constraints) {
      jarr.add(con.serializeJson());
    }
    
    return json;
  }
  
  public static DeploymentConfiguration fromJson(String jsonConfigStr) throws DeploymentException {
    return fromJson(JsonParser.parseString(jsonConfigStr).getAsJsonObject());
  }
  
  public static DeploymentConfiguration fromJson(JsonObject jsonConfig) throws DeploymentException {
    JsonObject jsonInfo = jsonConfig.getAsJsonObject("deploymentInfo");
    
    // FIXME improve this
    JsonElement jInstances = jsonInfo.get("deployComponentInstance");
    jsonInfo.remove("deployComponentInstance");
    jsonInfo.add("instances", jInstances);
    
    DeploymentInfo info = DeploymentInfo.fromJson(jsonInfo);
    List<Constraint> constraints = new ArrayList<>();
    
    JsonElement jeConstraints = jsonConfig.getAsJsonArray("deploymentConstraint");
    if(jeConstraints != null && jeConstraints.isJsonArray()) {
      JsonArray jConstraints = jeConstraints.getAsJsonArray();
      
      for(JsonElement jeConsraint : jConstraints) {
        JsonObject jConstraint = jeConsraint.getAsJsonObject();
        constraints.add(BasicConstraint.fromJson(jConstraint));
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
