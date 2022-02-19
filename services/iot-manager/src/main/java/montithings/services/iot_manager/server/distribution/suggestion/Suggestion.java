// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import com.google.common.collect.Lists;
import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Suggestion {
  
  public void applyTo(DeploymentConfiguration config);
  
  /**
   * Parses Prolog output for dropped constraints.
   * 
   * @return A parsed suggestion or null if it could not be parsed.
   */
  public static Suggestion parseProlog(String droppedMsg, List<String> instanceNames) {
    // register different suggestion types
    List<BiFunction<String, InstanceNameResolver, ? extends Suggestion>> providers = Lists.newArrayList(
        SuggestionGEQ::parseProlog,
        SuggestionEQ::parseProlog,
        SuggestionIncomp::parseProlog,
        SuggestionDependency::parseProlog,
        SuggestionHardware::parseProlog
    );
    
    // create instance name resolver to resolve prolog names to MontiThings instance names
    InstanceNameResolver resolver = new InstanceNameResolver(instanceNames);
    
    // test if droppedMsg can be parsed to any registered type of suggestion.
    for (BiFunction<String, InstanceNameResolver, ? extends Suggestion> provider : providers) {
      Suggestion sugg = provider.apply(droppedMsg, resolver);
      if (sugg != null) {
        return sugg;
      }
    }
    
    System.err.println("Could not parse: "+droppedMsg);
    
    // message could not be parsed
    return null;
  }
  
  /**
   * Parses Prolog location string (e.g. building1_floor1_room101). Not every
   * portion of this string must be specified. For example, building1_floor1 is
   * also a valid location specifier.
   */
  public static LocationSpecifier parseLocation(String prologStr) {
    LocationSpecifier location = new LocationSpecifier();
    
    // register location specifiers
    Map<String, Consumer<String>> pats = new HashMap<>();
    pats.put("building", location::setBuilding);
    pats.put("floor", location::setFloor);
    pats.put("room", location::setRoom);
    
    // look for location specifiers in prologStr and apply the corresponding
    // setters
    for (Entry<String, Consumer<String>> e : pats.entrySet()) {
      String pathSpec = e.getKey();
      Matcher mat = Pattern.compile(pathSpec + "([^_\\s]+)").matcher(prologStr);
      if (mat.find()) {
        // apply found value
        e.getValue().accept(mat.group(1));
      }
    }
    
    return location;
  }
  
}
