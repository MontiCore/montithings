package montithings.services.iot_manager.server.distribution.suggestion;

import java.util.Arrays;

import org.jpl7.Term;
import org.jpl7.Util;

import com.google.gson.JsonObject;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

public class SuggestionHardware implements Suggestion {
  
  public final String compName;
  public final String[] hardware;
  public final LocationSpecifier location;
  public final int count;
  
  public SuggestionHardware(String compName, String[] hardware, LocationSpecifier location, int count) {
    this.compName = compName;
    this.hardware = hardware;
    this.location = location;
    this.count = count;
  }
  
  @Override
  public void applyTo(DeploymentConfiguration config) {
    config.getHardwareSuggestions().add(this);
  }
  
  @Override
  public String toString() {
    return "SuggestionHardware [compName=" + compName + ", hardware=" + Arrays.toString(hardware) + ", location=" + location + ", count=" + count + "]";
  }
  
  public static SuggestionHardware parseProlog(String droppedMsg, InstanceNameResolver resolver) {
    if (droppedMsg.startsWith("suggestion_hardware(")) {
      try {
        Term term = Util.textToTerm(droppedMsg);
        String compName = resolver.resolveFromPrologName(term.arg(1).name());
        String[] hardware = Util.atomListToStringArray(term.arg(2));
        LocationSpecifier loc = Suggestion.parseLocation(term.arg(3).name());
        int count = term.arg(4).intValue();
        return new SuggestionHardware(compName, hardware, loc, count);
      }
      catch (Exception e) {
        return null;
      }
    }
    else {
      return null;
    }
  }
  
  public JsonObject serialize() {
    StringBuilder hwStr = new StringBuilder();
    if(hardware.length > 0) {
      for(int hwi = 0; hwi < hardware.length; hwi++) {
        String hw = hardware[hwi];
        hwStr.append(hw);
        if(hwi != hardware.length - 1) {
          hwStr.append(", ");
        }
      }
    } else {
      hwStr.append("n/a");
    }
    
    JsonObject jo = new JsonObject();
    jo.addProperty("compName", compName);
    jo.addProperty("hardware", hwStr.toString());
    jo.addProperty("hwBuildingSelector", location.getBuilding());
    jo.addProperty("hwFloorSelector", location.getFloor());
    jo.addProperty("hwRoomSelector", location.getRoom());
    jo.addProperty("hwCount", count);
    
    return jo;
  }
  
}
