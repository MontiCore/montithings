// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.constraint.AdaptedDependencyConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;
import montithings.services.iot_manager.server.data.constraint.DependencyConstraint;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dependency == null) ? 0 : dependency.hashCode());
    result = prime * result + ((dependent == null) ? 0 : dependent.hashCode());
    result = prime * result + (distinct ? 1231 : 1237);
    result = prime * result + orgCount;
    result = prime * result + satCount;
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
    SuggestionDependency other = (SuggestionDependency) obj;
    if (dependency == null) {
      if (other.dependency != null)
        return false;
    }
    else if (!dependency.equals(other.dependency))
      return false;
    if (dependent == null) {
      if (other.dependent != null)
        return false;
    }
    else if (!dependent.equals(other.dependent))
      return false;
    if (distinct != other.distinct)
      return false;
    if (orgCount != other.orgCount)
      return false;
    if (satCount != other.satCount)
      return false;
    return true;
  }
  
}
