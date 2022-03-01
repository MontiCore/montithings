// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import com.google.common.base.Objects;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint.Type;
import montithings.services.iot_manager.server.data.constraint.Constraint;

import java.util.ListIterator;

public class BasicSuggestion implements Suggestion {
  
  protected final String instanceName;
  protected final LocationSpecifier location;
  protected final int orgCount;
  protected final int satCount;
  protected final Type type;
  
  /**
   * @param satCount The count that would be satisfiable or -1 if there is not
   *          such (sensible) value.
   */
  public BasicSuggestion(String instanceName, LocationSpecifier location, int orgCount, int satCount, Type type) {
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
  
  public LocationSpecifier getLocation() {
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
        Objects.equal(con.getBuildingSelector(), this.location.getBuilding()) &&
        Objects.equal(con.getFloorSelector(), this.location.getFloor()) &&
        Objects.equal(con.getRoomSelector(), this.location.getRoom()) &&
        con.getConstraintType() == this.type;
  }
  
  @Override
  public void applyTo(DeploymentConfiguration config) {
    ListIterator<Constraint> it = config.getConstraints().listIterator();
    boolean foundMatch = false;
    while(it.hasNext()) {
      Constraint con = it.next();
      if(con instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) con;
        if(this.matches(bcon)) {
          // replace original constraint
          it.remove();
          foundMatch = true;
          // If the satCount is -1, the constraint should be removed completely.
          if(this.satCount != -1) {            
            it.add(bcon.withAlteredReference(this.satCount));
          }
        }
      }
    }
    
    // If we did not find a match, we'll add this as a new constraint instead.
    // This can happen for example, when the EVERY wildcard is used.
    if(!foundMatch) {
      it.add(new BasicConstraint(instanceName, type, orgCount, location.getBuilding(), location.getFloor(), location.getRoom()).withAlteredReference(this.satCount));      
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + orgCount;
    result = prime * result + satCount;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BasicSuggestion other = (BasicSuggestion) obj;
    if (instanceName == null) {
      if (other.instanceName != null)
        return false;
    }
    else if (!instanceName.equals(other.instanceName))
      return false;
    if (location == null) {
      if (other.location != null)
        return false;
    }
    else if (!location.equals(other.location))
      return false;
    if (orgCount != other.orgCount)
      return false;
    if (satCount != other.satCount)
      return false;
    if (type != other.type)
      return false;
    return true;
  }
  
}
