package ps.deployment.server.data;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ps.deployment.server.exception.DeploymentException;

public class DeploymentInfo {
  
  private InstanceInfo[] instances;
  
  public DeploymentInfo(InstanceInfo... instances) {
    this.setInstances(instances);
  }
  
  public InstanceInfo[] getInstances() {
    return instances;
  }
  
  public void setInstances(InstanceInfo[] instances) {
    this.instances = instances;
  }
  
  public List<String> getInstanceNames() {
    List<String> res = new LinkedList<>();
    
    for (InstanceInfo instance : instances) {
      res.add(instance.getInstanceName());
    }
    
    return res;
  }
  
  public static DeploymentInfo fromJson(JsonObject json) throws DeploymentException {
    try {
      LinkedList<InstanceInfo> instances = new LinkedList<>();
      
      JsonArray jInstances = json.getAsJsonArray("instances");
      if (jInstances == null || !jInstances.isJsonArray()) {
        // TODO throw
      }
      
      for (JsonElement jeInstance : jInstances) {
        JsonObject jInstance = jeInstance.getAsJsonObject();
        String componentType = jInstance.get("componentType").getAsString();
        String instanceName = jInstance.get("instanceName").getAsString();
        
        List<String> requirements = new LinkedList<String>();
        JsonElement jeReqs = jInstance.get("requirements");
        // collect requirements
        if (jeReqs != null && jeReqs.isJsonArray()) {
          JsonArray jReqs = jeReqs.getAsJsonArray();
          for (JsonElement jReq : jReqs) {
            requirements.add(jReq.getAsString());
          }
        }
        
        instances.add(new InstanceInfo(componentType, instanceName, requirements.toArray(new String[requirements.size()])));
      }
      
      return new DeploymentInfo(instances.toArray(new InstanceInfo[instances.size()]));
    } catch(ClassCastException | NullPointerException e) {
      throw new DeploymentException(e);
    }
  }
  
}
