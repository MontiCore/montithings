// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;
import montithings.services.iot_manager.server.exception.DeploymentException;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class BasicConstraint implements Constraint {
  
  private String instanceSelector;
  @SerializedName("type")
  private BasicConstraint.Type constraintType;
  private int referenceValue;
  
  private String buildingSelector;
  private String floorSelector;
  private String roomSelector;
  
  public BasicConstraint() {
    
  }
  
  public BasicConstraint(String instanceSelector, Type constraintType, int referenceValue, String buildingSelector, String floorSelector, String roomSelector) {
    super();
    this.instanceSelector = instanceSelector;
    this.constraintType = constraintType;
    this.referenceValue = referenceValue;
    this.buildingSelector = buildingSelector;
    this.floorSelector = floorSelector;
    this.roomSelector = roomSelector;
  }
  
  /**
   * @return whether the location componenent {@code spec} (building/floor/room)
   *         is bound, i.e. whether it should be considered for constraints.
   */
  private boolean isLocationComponentBound(String spec) {
    return spec != null && !spec.trim().isEmpty();
  }
  
  /**
   * Constructs a location specifier from building-, floor- & room-selectors for
   * the Prolog generator.
   * 
   * @return a {@link String} like "building1_floor4_room2"
   */
  private String getMergedLocation() {
    StringBuilder sb = new StringBuilder();
    
    LinkedHashMap<String, String> selectors = new LinkedHashMap<>();
    selectors.put("building", buildingSelector);
    selectors.put("floor", floorSelector);
    selectors.put("room", roomSelector);
    
    for (Entry<String, String> entry : selectors.entrySet()) {
      if (isLocationComponentBound(entry.getValue())) {
        sb.append(entry.getKey()).append(entry.getValue()).append("_");
      }
    }
    
    // remove last underscore
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    
    return sb.toString();
  }
  
  @Override
  public void applyConstraint(DeployConfigBuilder builder) {
    // construct constraint like ["location", "building1_floor1", ">=", 1]
    JsonArray json = new JsonArray();
    json.add("location");
    json.add(getMergedLocation());
    json.add(constraintType.value);
    json.add(referenceValue);
    
    // add the constraint to the instance
    builder.distributionConstraintsFor(instanceSelector).add(json);
  }
  
  public LocationSpecifier getLocationSpecifier() {
    return new LocationSpecifier(buildingSelector, floorSelector, roomSelector);
  }
  
  public static BasicConstraint fromJson(JsonObject json) throws DeploymentException {
    BasicConstraint con = new BasicConstraint();
    con.instanceSelector = json.get("instanceSelector").getAsString();
    
    String constraintStr = json.get("type").getAsString();
    con.constraintType = Type.forName(constraintStr);
    if (con.constraintType == null)
      throw new DeploymentException("Invalid constraint type '" + constraintStr + "' for constraint.");
    
    con.referenceValue = json.get("referenceValue").getAsInt();
    
    con.buildingSelector = json.get("buildingSelector").getAsString();
    con.floorSelector = json.get("floorSelector").getAsString();
    con.roomSelector = json.get("roomSelector").getAsString();
    
    return con;
  }
  
  @Override
  public JsonObject serializeJson() {
    //JsonObject json = new JsonObject();
    
    /*json.addProperty("instanceSelector", this.instanceSelector);
    json.addProperty("referenceValue", this.referenceValue);
    json.addProperty("buildingSelector", this.buildingSelector);
    json.addProperty("floorSelector", this.floorSelector);
    json.addProperty("roomSelector", this.roomSelector);*/
    return new Gson().toJsonTree(this).getAsJsonObject();
  }
  
  public Constraint withAlteredReference(int refCount) {
    return new AdaptedBasicConstraint(instanceSelector, constraintType, refCount, buildingSelector, floorSelector, roomSelector, this.referenceValue);
  }
  
  public String getInstanceSelector() {
    return instanceSelector;
  }
  
  public void setInstanceSelector(String instanceSelector) {
    this.instanceSelector = instanceSelector;
  }
  
  public BasicConstraint.Type getConstraintType() {
    return constraintType;
  }
  
  public void setConstraintType(BasicConstraint.Type constraintType) {
    this.constraintType = constraintType;
  }
  
  public int getReferenceValue() {
    return referenceValue;
  }
  
  public void setReferenceValue(int referenceValue) {
    this.referenceValue = referenceValue;
  }
  
  public String getBuildingSelector() {
    return buildingSelector;
  }
  
  public void setBuildingSelector(String buildingSelector) {
    this.buildingSelector = buildingSelector;
  }
  
  public String getFloorSelector() {
    return floorSelector;
  }
  
  public void setFloorSelector(String floorSelector) {
    this.floorSelector = floorSelector;
  }
  
  public String getRoomSelector() {
    return roomSelector;
  }
  
  public void setRoomSelector(String roomSelector) {
    this.roomSelector = roomSelector;
  }
  
  @Override
  public BasicConstraint clone() {
    return new BasicConstraint(instanceSelector, constraintType, referenceValue, buildingSelector, floorSelector, roomSelector);
  }
  
  public static enum Type {
    EQUALS("=="), LESS_EQUAL("<="), GREATER_EQUAL(">=");
    
    public final String value;
    
    private Type(String value) {
      this.value = value;
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
  
  @Override
  public String toString() {
    return "BasicConstraint [instanceSelector=" + instanceSelector + ", constraintType=" + constraintType + ", referenceValue=" + referenceValue + ", buildingSelector=" + buildingSelector + ", floorSelector=" + floorSelector + ", roomSelector=" + roomSelector + "]";
  }
  
}
