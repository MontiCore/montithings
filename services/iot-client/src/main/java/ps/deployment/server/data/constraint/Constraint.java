package ps.deployment.server.data.constraint;

import com.google.gson.JsonObject;

import ps.deployment.server.distribution.config.DeployConfigBuilder;

public interface Constraint {
  
  public void applyConstraint(DeployConfigBuilder builder);
  public JsonObject serializeJson();
  
}
