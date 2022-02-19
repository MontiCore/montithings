// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;

public class LocationConstraint implements Constraint {
  
  private final String instanceName;
  private final Type constraintType;
  
  private final String buildingSelector;
  private final String floorSelector;
  private final String roomSelector;
  
  public LocationConstraint(String instanceName, Type constraintType, String buildingSelector, String floorSelector, String roomSelector) {
    super();
    this.instanceName = instanceName;
    this.constraintType = constraintType;
    this.buildingSelector = buildingSelector;
    this.floorSelector = floorSelector;
    this.roomSelector = roomSelector;
  }
  
  public String getInstanceName() {
    return instanceName;
  }
  
  public Type getConstraintType() {
    return constraintType;
  }
  
  public String getBuildingSelector() {
    return buildingSelector;
  }
  
  public String getFloorSelector() {
    return floorSelector;
  }
  
  public String getRoomSelector() {
    return roomSelector;
  }
  
  @Override
  public void applyConstraint(DeployConfigBuilder builder) {
    // Constructs an array like ["location", "building1", 1].
    // For more information, consult the documentation of the Prolog-Generator.
    JsonArray json = new JsonArray(3);
    json.add("location");
    json.add(LocationSpecifier.create(buildingSelector, floorSelector, roomSelector).toPrologString());
    json.add(this.constraintType.prologValue);
    builder.distributionSelectionFor(instanceName).add(json);
  }
  
  @Override
  public JsonObject serializeJson() {
    return new Gson().toJsonTree(this).getAsJsonObject();
  }
  
  public static LocationConstraint fromJson(JsonObject json) {
    String instanceName = json.get("instanceName").getAsString();
    Type constraintType = Type.forName(json.get("constraintType").getAsString());
    
    String buildingSelector = json.get("buildingSelector").getAsString();
    String floorSelector = json.get("floorSelector").getAsString();
    String roomSelector = json.get("roomSelector").getAsString();
    
    return new LocationConstraint(instanceName, constraintType, buildingSelector, floorSelector, roomSelector);
  }
  
  @Override
  public String toString() {
    return "LocationConstraint [instanceName=" + instanceName + ", constraintType=" + constraintType + ", buildingSelector=" + buildingSelector + ", floorSelector=" + floorSelector + ", roomSelector=" + roomSelector + "]";
  }
  
  public static enum Type {
    WHITELIST(0), BLACKLIST(1);
    
    public final int prologValue;
    
    private Type(int prologValue) {
      this.prologValue = prologValue;
    }
    
    public static Type forName(String value) {
      for (Type t : values()) {
        if (t.name().equals(value)) {
          return t;
        }
      }
      return null;
    }
  }
  
}
