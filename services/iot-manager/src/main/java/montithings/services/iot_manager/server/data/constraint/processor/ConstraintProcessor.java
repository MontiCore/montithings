// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.distribution.IDistributionCalculator;

/**
 * ConstraintProcessors are used to preprocess constraints. For an example: See
 * {@link BasicConstraintEveryWildcardProcessor}.
 */
public interface ConstraintProcessor {
  
  /** Called before being fed to a {@link IDistributionCalculator} */
  public void apply(ConstraintContext context, DeploymentConfiguration target);
  
  /** Called after being fed to a {@link IDistributionCalculator} */
  public void clean(ConstraintContext context, DeploymentConfiguration target);
  
}
