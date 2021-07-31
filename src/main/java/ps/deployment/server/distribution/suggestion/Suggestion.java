package ps.deployment.server.distribution.suggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import ps.deployment.server.data.LocationSpecifier;
import ps.deployment.server.data.DeploymentConfiguration;

public interface Suggestion {
  
  public void applyTo(DeploymentConfiguration config);
  
  /**
   * Parses Prolog output for dropped constraints.
   * 
   * @return A parsed suggestion or null if it could not be parsed.
   */
  public static Suggestion parseProlog(String droppedMsg) {
    // register different suggestion types
    List<Function<String, ? extends Suggestion>> providers = Lists.newArrayList(
        SuggestionGEQ::parseProlog,
        SuggestionEQ::parseProlog,
        SuggestionIncomp::parseProlog,
        SuggestionDependency::parseProlog
    );
    
    // test if droppedMsg can be parsed to any registered type of suggestion.
    for (Function<String, ? extends Suggestion> provider : providers) {
      Suggestion sugg = provider.apply(droppedMsg);
      if (sugg != null) {
        return sugg;
      }
    }
    
    System.err.println("Could not parse: "+droppedMsg);
    
    // message could not be parsed
    return null;
  }
  
  /**
   * Transforms an instance name from Prolog format (e.g. HierarchyExampleSink)
   * to its model name (e.g. hierarchy.example.sink)
   */
  public static String transformInstanceName(String prologName) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < prologName.length(); i++) {
      char c = prologName.charAt(i);
      if (Character.isUpperCase(c)) {
        // if this character is in upper case, also insert a dot
        if (sb.length() > 0) {
          // the first character should not be preceded by a dot
          sb.append('.');
        }
        
        sb.append(Character.toLowerCase(c));
      }
      else {
        // if this character is in lower case, just append it
        sb.append(c);
      }
    }
    return sb.toString();
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
