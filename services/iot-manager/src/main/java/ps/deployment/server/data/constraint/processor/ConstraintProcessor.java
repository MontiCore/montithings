// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.data.constraint.processor;

import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.distribution.IDistributionCalculator;

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
