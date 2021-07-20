package ps.deployment.server.distribution.suggestion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ps.deployment.server.data.LocationSpecifier;
import ps.deployment.server.data.constraint.BasicConstraint.Type;

public class SuggestionGEQ extends BasicSuggestion {
  
  private static final Pattern patternProlog = Pattern.compile("\\[GEQ\\] (?<instanceName>[\\w]+) location (?<locationSpec>[\\w]+) >= (?<orgCount>\\d+) \\((?<satCount>\\d+).*\\)");
  
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
  
  public static SuggestionGEQ parseProlog(String droppedMsg) {
    Matcher matcher = patternProlog.matcher(droppedMsg);
    if (matcher.find()) {
      String instanceName = Suggestion.transformInstanceName(matcher.group("instanceName"));
      LocationSpecifier locationSpec = Suggestion.parseLocation(matcher.group("locationSpec"));
      int orgCount = Integer.parseInt(matcher.group("orgCount"));
      int satCount = Integer.parseInt(matcher.group("satCount"));
      return new SuggestionGEQ(instanceName, locationSpec, orgCount, satCount);
    }
    else {
      return null;
    }
  }
  
}
