// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import montithings.services.iot_manager.server.data.DeployClient;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;

import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

/**
 * Resolves "every" location wildcards, i.e. removes constraints containing
 * "every" wildcards and inserts concrete locations instead.
 * 
 * @see ConstraintProcessor
 */
public class BasicConstraintEveryWildcardProcessor implements ConstraintProcessor {
  
  private static final String WILDCARD_LITERAL = "EVERY";
  
  @Override
  public void apply(ConstraintContext context, DeploymentConfiguration target) {
    ListIterator<Constraint> it = target.getConstraints().listIterator();
    
    while(it.hasNext()) {
      Constraint constraint = it.next();
      if(constraint instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) constraint;
        LocationSpecifier loc = bcon.getLocationSpecifier();
        if(containsWildcard(loc)) {
          // This constraint contains a wildcard. Remove it and add constraint for every fitting real location.
          it.remove();
          for(LocationSpecifier subloc : getMatchingLocations(context, loc)) {
            BasicConstraint newCon = bcon.clone();
            newCon.setBuildingSelector(subloc.getBuilding());
            newCon.setFloorSelector(subloc.getFloor());
            newCon.setRoomSelector(subloc.getRoom());
            // If there explicitly is a constraint that is more specific than this one, we'll not insert one for this location.
            if(!containsConstraintLike(target, newCon)) {
              // construct new constraint
              // add it to the constraint list
              it.add(newCon);
            }
          }
        }
      }
    }
  }
  
  private boolean containsConstraintLike(DeploymentConfiguration config, BasicConstraint reference) {
    for(Constraint con : config.getConstraints()) {
      if(con instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) con;
        if(reference.getInstanceSelector().equals(bcon.getInstanceSelector()) 
            && bcon.getLocationSpecifier().equals(reference.getLocationSpecifier()) 
            && bcon.getConstraintType() == reference.getConstraintType()) {
          System.out.println(reference+" / "+con+": match");
          return true;
        }
      }
    }
    System.out.println(reference+": no match");
    return false;
  }
  
  private boolean containsWildcard(LocationSpecifier loc) {
    return loc.toPrologString().contains(WILDCARD_LITERAL);
  }
  
  /**
   * @return true iff sub matches ref with regard to "every" wildcards.
   * For example: "building1_floor2_room3" is a sub location of "building1_floor2_roomEVERY". 
   * */
  private boolean isSubLocation(LocationSpecifier ref, LocationSpecifier sub) {
    boolean matches = true;
    if(ref.getBuilding() != null && !WILDCARD_LITERAL.equals(ref.getBuilding())) {
      matches &= ref.getBuilding().equals(sub.getBuilding());
    }
    if(ref.getFloor() != null && !WILDCARD_LITERAL.equals(ref.getFloor())) {
      matches &= ref.getFloor().equals(sub.getFloor());
    }
    if(ref.getRoom() != null && !WILDCARD_LITERAL.equals(ref.getRoom())) {
      matches &= ref.getRoom().equals(sub.getRoom());
    }
    return matches;
  }
  
  /**
   * Replaces wildcards with concrete values, e.g. for
   * target=buildingEVERY_floorANY_room3 and ref=building1_floor2_room3, the
   * result is building_1_floorANY_room3.
   */
  private LocationSpecifier replaceWildcards(LocationSpecifier target, LocationSpecifier ref) {
    LocationSpecifier loc = new LocationSpecifier();
    loc.setBuilding(WILDCARD_LITERAL.equals(target.getBuilding()) ? ref.getBuilding() : target.getBuilding());
    loc.setFloor(WILDCARD_LITERAL.equals(target.getFloor()) ? ref.getFloor() : target.getFloor());
    loc.setRoom(WILDCARD_LITERAL.equals(target.getRoom()) ? ref.getRoom() : target.getRoom());
    return loc;
  }
  
  /**
   * Finds all locations of DeployClients that match the LocationSpecifier loc.
   * @param ctx The context
   * @param loc The {@link LocationSpecifier} (potentially) containing "every" wildcards
   */
  private Set<LocationSpecifier> getMatchingLocations(ConstraintContext ctx, LocationSpecifier loc) {
    Set<LocationSpecifier> locations = new HashSet<>();
    for(DeployClient client : ctx.getClients()) {
      if(isSubLocation(loc, client.getLocation())) {
        locations.add(replaceWildcards(loc, client.getLocation()));
      }
    }
    return locations;
  }
  
  @Override
  public void clean(ConstraintContext context, DeploymentConfiguration target) {
    
  }
  
}
