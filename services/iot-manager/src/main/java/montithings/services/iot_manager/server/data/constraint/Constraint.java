// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint;

import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.distribution.config.DeployConfigBuilder;

public interface Constraint {
  
  public void applyConstraint(DeployConfigBuilder builder);
  public JsonObject serializeJson();
  
}
