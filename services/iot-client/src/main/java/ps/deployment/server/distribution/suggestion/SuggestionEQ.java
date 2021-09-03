package ps.deployment.server.distribution.suggestion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ps.deployment.server.data.LocationSpecifier;
import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.data.constraint.BasicConstraint.Type;
import ps.deployment.server.util.InstanceNameResolver;

public class SuggestionEQ extends BasicSuggestion {
  
private static final Pattern patternProlog = Pattern.compile("\\[EQ\\] (?<instanceName>[\\wäöüÄÖÜß]+) location (?<locationSpec>[\\wäöüÄÖÜß]*) == (?<orgCount>\\d+)");
  
  public SuggestionEQ(String instanceName, LocationSpecifier location, int orgCount) {
    // Satisfiable count is set to -1 since it is unclear whether it would be
    // better to increase or decrease the reference count. The constraint will
    // be removed completely for the suggestion.
    super(instanceName, location, orgCount, -1, Type.EQUALS);
  }
  
  public static Pattern getPatternProlog() {
    return patternProlog;
  }
  
  @Override
  public String toString() {
    return "SuggestionEQ [instanceName=" + instanceName + ", location=" + location + ", orgCount=" + orgCount + "]";
  }
  
  @Override
    public void applyTo(DeploymentConfiguration config) {
      super.applyTo(config);
    }
  
  public static SuggestionEQ parseProlog(String droppedMsg, InstanceNameResolver resolver) {
    Matcher matcher = patternProlog.matcher(droppedMsg);
    if (matcher.find()) {
      String instanceName = resolver.resolveFromPrologName(matcher.group("instanceName"));
      LocationSpecifier locationSpec = Suggestion.parseLocation(matcher.group("locationSpec"));
      int orgCount = Integer.parseInt(matcher.group("orgCount"));
      return new SuggestionEQ(instanceName, locationSpec, orgCount);
    }
    else {
      return null;
    }
  }
  
}
