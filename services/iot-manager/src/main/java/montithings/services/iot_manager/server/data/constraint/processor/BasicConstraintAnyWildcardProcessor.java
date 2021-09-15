// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;

/**
 * Resolves "every" location wildcards, i.e. removes constraints containing
 * "every" wildcards and inserts concrete locations instead.
 * 
 * @see ConstraintProcessor
 */
public class BasicConstraintAnyWildcardProcessor implements ConstraintProcessor {
  
  private static final String WILDCARD_LITERAL = "ANY";
  
  @Override
  public void apply(ConstraintContext context, DeploymentConfiguration target) {
    for (Constraint con : target.getConstraints()) {
      if (con instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) con;
        // Every location component with value "ANY" will be set to null,
        // because the prolog-generator implicitly handles absent values as
        // "any" value.
        if (WILDCARD_LITERAL.equals(bcon.getBuildingSelector())) {
          bcon.setBuildingSelector(null);
        }
        if (WILDCARD_LITERAL.equals(bcon.getFloorSelector())) {
          bcon.setFloorSelector(null);
        }
        if (WILDCARD_LITERAL.equals(bcon.getRoomSelector())) {
          bcon.setRoomSelector(null);
        }
      }
    }
  }
  
  @Override
  public void clean(ConstraintContext context, DeploymentConfiguration target) {
    for (Constraint con : target.getConstraints()) {
      if (con instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) con;
        // See comment in apply function.
        if (bcon.getBuildingSelector() == null) {
          bcon.setBuildingSelector(WILDCARD_LITERAL);
        }
        if (bcon.getFloorSelector() == null) {
          bcon.setFloorSelector(WILDCARD_LITERAL);
        }
        if (bcon.getRoomSelector() == null) {
          bcon.setRoomSelector(WILDCARD_LITERAL);
        }
      }
    }
  }
  
}
