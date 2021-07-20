package ps.deployment.server.data.constraint;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import ps.deployment.server.distribution.config.DeployConfigBuilder;

public class DependencyConstraint implements Constraint {
  @SerializedName("dependentInstanceName")
  private String dependent;
  @SerializedName("dependencyInstanceName")
  private String dependency;
  @SerializedName("count")
  private int count;
  @SerializedName("exclusive")
  private boolean exclusive;
  
  public DependencyConstraint(String dependent, String dependency, int count, boolean exclusive) {
    super();
    this.dependent = dependent;
    this.dependency = dependency;
    this.count = count;
    this.exclusive = exclusive;
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
  
  @Override
  public void applyConstraint(DeployConfigBuilder builder) {
    JsonObject json = new JsonObject();
    json.addProperty("type", this.exclusive ? "distinct" : "simple");
    json.addProperty("dependent", this.dependent);
    json.addProperty("dependency", this.dependency);
    json.addProperty("amount_at_least", this.count);
    builder.dependencies().add(json);
  }
  
  @Override
  public String toString() {
    return "DependencyConstraint [dependent=" + dependent + ", dependency=" + dependency + ", count=" + count + ", exclusive=" + exclusive + "]";
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
    return new DependencyConstraint(dependent, dependency, count, exclusive);
  }
  
}
