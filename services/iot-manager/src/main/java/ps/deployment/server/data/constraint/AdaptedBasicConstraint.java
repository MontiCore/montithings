// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.data.constraint;

import com.google.gson.JsonObject;

public class AdaptedBasicConstraint extends BasicConstraint {
  
  private int originalRefValue;
  
  public AdaptedBasicConstraint() {
    super();
  }
  
  public AdaptedBasicConstraint(String instanceSelector, Type constraintType, int referenceValue, String buildingSelector, String floorSelector, String roomSelector, int originalRefValue) {
    super(instanceSelector, constraintType, referenceValue, buildingSelector, floorSelector, roomSelector);
    this.originalRefValue = originalRefValue;
  }
  
  @Override
  public JsonObject serializeJson() {
    JsonObject json = super.serializeJson();
    json.addProperty("originalRefValue", this.originalRefValue);
    return json;
  }
  
}
