// (c) https://github.com/MontiCore/monticore
package ps.deployment.server.distribution.suggestion;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ps.deployment.server.data.DeploymentConfiguration;
import ps.deployment.server.data.constraint.AdaptedDependencyConstraint;
import ps.deployment.server.data.constraint.Constraint;
import ps.deployment.server.data.constraint.DependencyConstraint;
import ps.deployment.server.util.InstanceNameResolver;

public class SuggestionDependency implements Suggestion {
  
  private static final Pattern patternProlog = Pattern.compile("\\[DEP(?<distinct>-DIST)?\\] \"(?<dependent>[^\"]+)\"[^\\d]+(?<orgCount>\\d+)?[^\"]+\"(?<dependency>[^\"]+)\"\\s*\\((?<satCount>\\d+).*");
  
  private final String dependent;
  private final String dependency;
  private final boolean distinct;
  private final int satCount, orgCount;
  
  public SuggestionDependency(String dependent, String dependency, int satCount, int orgCount, boolean distinct) {
    this.dependent = dependent;
    this.dependency = dependency;
    this.satCount = satCount;
    this.orgCount = orgCount;
    this.distinct = distinct;
  }
  
  private boolean matches(DependencyConstraint con) {
    return con.getDependent().equalsIgnoreCase(dependent)
        && con.getDependency().equalsIgnoreCase(dependency)
        && con.isExclusive() == this.distinct
        && con.getCount() == this.orgCount;
  }
  
  @Override
  public void applyTo(DeploymentConfiguration config) {
    ListIterator<Constraint> it = config.getConstraints().listIterator();
    while(it.hasNext()) {
      Constraint con = it.next();
      if(con instanceof DependencyConstraint) {
        DependencyConstraint dcon = (DependencyConstraint) con;
        if(this.matches(dcon)) {
          // replace original constraint with dropped one
          it.remove();
          it.add(new AdaptedDependencyConstraint(dependent, dependency, satCount, distinct, dcon.getLocationType(), orgCount));
        }
      }
    }
  }
  
  public static SuggestionDependency parseProlog(String droppedMsg, InstanceNameResolver resolver) {
    Matcher matcher = patternProlog.matcher(droppedMsg);
    if (matcher.find()) {
      String dependent = resolver.resolveFromPrologName(matcher.group("dependent"));
      String dependency = resolver.resolveFromPrologName(matcher.group("dependency"));
      int satCount = Integer.parseInt(matcher.group("satCount"));
      int orgCount = Integer.parseInt(matcher.group("orgCount"));
      boolean distinct = matcher.group("distinct") != null;
      return new SuggestionDependency(dependent, dependency, satCount, orgCount, distinct);
    }
    else {
      return null;
    }
  }
  
}
