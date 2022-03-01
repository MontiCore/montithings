// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import montithings.services.iot_manager.server.data.LocationSpecifier;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint.Type;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestionGEQ extends BasicSuggestion {
  
  private static final Pattern patternProlog = Pattern.compile("\\[GEQ\\] (?<instanceName>[\\wäöüÄÖÜß]+) location (?<locationSpec>[\\wäöüÄÖÜß]*) >= (?<orgCount>\\d+) \\((?<satCount>\\d+).*\\)");
  
  public SuggestionGEQ(String instanceName, LocationSpecifier location, int orgCount, int satCount) {
    super(instanceName, location, orgCount, satCount, Type.GREATER_EQUAL);
  }
  
  public static Pattern getPatternProlog() {
    return patternProlog;
  }
  
  @Override
  public String toString() {
    return "SuggestionGEQ [instanceName=" + instanceName + ", location=" + location + ", orgCount=" + orgCount + ", satCount=" + satCount + "]";
  }
  
  public static SuggestionGEQ parseProlog(String droppedMsg, InstanceNameResolver resolver) {
    Matcher matcher = patternProlog.matcher(droppedMsg);
    if (matcher.find()) {
      String instanceName = resolver.resolveFromPrologName(matcher.group("instanceName"));
      LocationSpecifier locationSpec = Suggestion.parseLocation(matcher.group("locationSpec"));
      int orgCount = Integer.parseInt(matcher.group("orgCount"));
      int satCount = Integer.parseInt(matcher.group("satCount"));
      return new SuggestionGEQ(instanceName, locationSpec, orgCount, satCount);
    }
    else {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
}
