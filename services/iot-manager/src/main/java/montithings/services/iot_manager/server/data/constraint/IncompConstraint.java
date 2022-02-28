// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;

public class IncompConstraint implements Constraint {
  
  @SerializedName("instanceSelector1")
  private final String instanceName1;
  @SerializedName("instanceSelector2")
  private final String instanceName2;
  @SerializedName("dropped")
  private final boolean dropped;
  
  public IncompConstraint(String instanceName1, String instanceName2, boolean dropped) {
    super();
    this.instanceName1 = instanceName1;
    this.instanceName2 = instanceName2;
    this.dropped = dropped;
  }
  
  public String getInstanceName1() {
    return instanceName1;
  }
  
  public String getInstanceName2() {
    return instanceName2;
  }
  
  @Override
  public void applyConstraint(DeployConfigBuilder builder) {
    JsonArray json = new JsonArray();
    json.add(this.instanceName1);
    json.add(this.instanceName2);
    builder.incompatibilities().add(json);
  }
  
  @Override
  public JsonObject serializeJson() {
    return new Gson().toJsonTree(this).getAsJsonObject();
  }
  
  public static IncompConstraint fromJson(JsonObject json) {
    String instanceName1 = json.get("instanceSelector1").getAsString();
    String instanceName2 = json.get("instanceSelector2").getAsString();
    return new IncompConstraint(instanceName1, instanceName2, false);
  }
  
  @Override
  public String toString() {
    return "LocationConstraint [instanceName1=" + instanceName1 + ", instanceName2=" + instanceName2 + "]";
  }
  
}
