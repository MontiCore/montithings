// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

  public InstanceInfo getInstanceInfo(String instanceName) {
    // transform to model instance name, e.g.: hierarchy.test__1 -> hierarchy.test
    String modelInstanceName = instanceName.replaceAll("__\\d+", "");
    for (InstanceInfo info : instances) {
      if (info.getInstanceName().equalsIgnoreCase(modelInstanceName)) {
        return info;
      }
    }
    return null;
  }

  public List<String> getInstanceNames() {
    List<String> res = new LinkedList<>();

    for (InstanceInfo instance : instances) {
      res.add(instance.getInstanceName());
    }

    return res;
  }

  public String getOutermostComponent() {
    String shortestInstanceName = "";
    int shortestLength = Integer.MAX_VALUE;

    for (InstanceInfo instance : instances) {
      int curLength = instance.getInstanceName().split("\\.").length;

      if (curLength < shortestLength) {
        shortestInstanceName = instance.getInstanceName();
        shortestLength = curLength;
      }
    }

    return shortestInstanceName;
  }

  public TerraformInfo[] getTerraformInfos() {
    List<TerraformInfo> res = new LinkedList<>();

    for (InstanceInfo instance : instances) {
      for (TerraformInfo terraformInfo : instance.getTerraformInfo()) {
        res.add(terraformInfo);
      }
    }

    return res.toArray(new TerraformInfo[res.size()]);

  }

  public static DeploymentInfo fromJson(JsonObject json) throws DeploymentException {
    try {
      LinkedList<InstanceInfo> instances = new LinkedList<>();

      JsonArray jInstances = json.getAsJsonArray("instances");
      if (jInstances == null || !jInstances.isJsonArray()) {
        throw new DeploymentException("Invalid deployment info.");
      }

      for (JsonElement jeInstance : jInstances) {
        JsonObject jInstance = jeInstance.getAsJsonObject();
        String componentType = jInstance.get("componentType").getAsString();
        String instanceName = jInstance.get("instanceName").getAsString();
        String hardwareRequirement = "";
        if (jInstance.has("hardwareRequirements") && !jInstance.get("hardwareRequirements").getAsString().isEmpty()) {
          hardwareRequirement = jInstance.get("hardwareRequirements").getAsString();
        }

        List<String> requirements = new LinkedList<String>();
        JsonElement jeReqs = jInstance.get("requirements");
        // collect requirements
        if (jeReqs != null && jeReqs.isJsonArray()) {
          JsonArray jReqs = jeReqs.getAsJsonArray();
          for (JsonElement jReq : jReqs) {
            requirements.add(jReq.getAsString());
          }
        }

        List<TerraformInfo> terraformInfos = new ArrayList<>();
        JsonElement jeTerraformInfo = jInstance.get("terraformInfo");
        if (jeTerraformInfo != null && jeTerraformInfo.isJsonArray()) {
          JsonArray jTerraformInfos = jeTerraformInfo.getAsJsonArray();
          for (JsonElement jTerraformInfo : jTerraformInfos) {
            JsonObject terraformInfo = jTerraformInfo.getAsJsonObject();
            String filename = terraformInfo.get("filename").getAsString();
            String filecontent = terraformInfo.get("filecontent").getAsString();
            terraformInfos.add(new TerraformInfo(filename, filecontent));
          }
        }

        instances.add(new InstanceInfo(
            componentType,
            instanceName,
            hardwareRequirement,
            requirements.toArray(new String[requirements.size()]),
            terraformInfos.toArray(new TerraformInfo[terraformInfos.size()])));
      }

      return new DeploymentInfo(instances.toArray(new InstanceInfo[instances.size()]));
    } catch (ClassCastException | NullPointerException e) {
      throw new DeploymentException(e);
    }
  }

}
