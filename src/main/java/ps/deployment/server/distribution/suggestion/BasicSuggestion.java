package ps.deployment.server.distribution.suggestion;

import java.util.ListIterator;
import ps.deployment.server.data.DeployClientLocation;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.data.constraint.BasicConstraint;
import ps.deployment.server.data.constraint.Constraint;
import ps.deployment.server.data.constraint.BasicConstraint.Type;

public class BasicSuggestion implements Suggestion {
  
  protected final String instanceName;
  protected final DeployClientLocation location;
  protected final int orgCount;
  protected final int satCount;
  protected final Type type;
  
  /**
   * @param satCount The count that would be satisfiable or -1 if there is not
   *          such (sensible) value.
   */
  public BasicSuggestion(String instanceName, DeployClientLocation location, int orgCount, int satCount, Type type) {
    super();
    this.instanceName = instanceName;
    this.location = location;
    this.orgCount = orgCount;
    this.satCount = satCount;
    this.type = type;
  }
  
  public String getInstanceName() {
    return instanceName;
  }
  
  public DeployClientLocation getLocation() {
    return location;
  }
  
  public int getOrgCount() {
    return orgCount;
  }
  
  public int getSatCount() {
    return satCount;
  }
  
  private boolean matches(BasicConstraint con) {
    return
        con.getInstanceSelector().toLowerCase().equals(this.instanceName) &&
        con.getReferenceValue() == this.orgCount &&
        con.getBuildingSelector().equals(this.location.getBuilding()) &&
        con.getFloorSelector().equals(this.location.getFloor()) &&
        con.getRoomSelector().equals(this.location.getRoom()) &&
        con.getConstraintType() == this.type;
  }
  
  @Override
  public void applyTo(DeploymentConfiguration config) {
    ListIterator<Constraint> it = config.getConstraints().listIterator();
    while(it.hasNext()) {
      Constraint con = it.next();
      if(con instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) con;
        if(this.matches(bcon)) {
          // replace original constraint
          it.remove();
          // If the satCount is -1, the constraint should be removed completely.
          if(this.satCount != -1) {            
            it.add(bcon.withAlteredReference(this.satCount));
          }
        }
      }
    }
  }
  
}
