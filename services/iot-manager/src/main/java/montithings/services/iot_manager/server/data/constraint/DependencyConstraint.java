// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;

public class DependencyConstraint implements Constraint {
  @SerializedName("dependentInstanceName")
  private String dependent;
  @SerializedName("dependencyInstanceName")
  private String dependency;
  @SerializedName("count")
  private int count;
  @SerializedName("exclusive")
  private boolean exclusive;
  @SerializedName("locationType")
  private LocationType locationType;
  
  public DependencyConstraint(String dependent, String dependency, int count, boolean exclusive, LocationType locType) {
    super();
    this.dependent = dependent;
    this.dependency = dependency;
    this.count = count;
    this.exclusive = exclusive;
    this.locationType = locType;
  }
  
  public String getDependent() {
    return dependent;
  }
  
  public String getDependency() {
    return dependency;
  }
  
  public int getCount() {
    return count;
  }
  
  public boolean isExclusive() {
    return exclusive;
  }
  
  public LocationType getLocationType() {
    return locationType;
  }
  
  @Override
  public void applyConstraint(DeployConfigBuilder builder) {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.exclusive ? "distinct" : "simple");
    json.addProperty("dependent", this.dependent);
    json.addProperty("dependency", this.dependency);
    json.addProperty("amount_at_least", this.count);
    json.addProperty("location", this.locationType.prologValue);
    builder.dependencies().add(json);
  }
  
  @Override
  public String toString() {
    return "DependencyConstraint [dependent=" + dependent + ", dependency=" + dependency + ", count=" + count + ", exclusive=" + exclusive + ", locationType=" + locationType +  "]";
  }
  
  @Override
  public JsonObject serializeJson() {
    return new Gson().toJsonTree(this).getAsJsonObject();
  }
  
  public static DependencyConstraint fromJson(JsonObject json) {
    String dependency = json.get("dependencyInstanceName").getAsString();
    String dependent = json.get("dependentInstanceName").getAsString();
    int count = json.get("count").getAsInt();
    boolean exclusive = json.get("exclusive").getAsBoolean();
    LocationType locType = LocationType.forName(json.get("locationType").getAsString());
    return new DependencyConstraint(dependent, dependency, count, exclusive, locType);
  }
  
  public static enum LocationType {
    ANY("any"), SAME_BUILDING("same_building"), SAME_FLOOR("same_floor"), SAME_ROOM("same_room");
    
    public final String prologValue;
    
    private LocationType(String prologValue) {
      this.prologValue = prologValue;
    }
    
    public static LocationType forName(String value) {
      for (LocationType t : values()) {
        if (t.name().equals(value)) {
          return t;
        }
      }
      return null;
    }
  }
  
}
