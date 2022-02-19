// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.constraint.Constraint;
import montithings.services.iot_manager.server.data.constraint.IncompConstraint;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuggestionIncomp implements Suggestion {
  
  private static final Pattern patternProlog = Pattern.compile("\\[INCOMP\\][^\"]+\"([^\"]+)\"[^\"]+\"([^\"]+)\"");
  
  private final String instanceName1;
  private final String instanceName2;
  
  public SuggestionIncomp(String instanceName1, String instanceName2) {
    this.instanceName1 = instanceName1;
    this.instanceName2 = instanceName2;
  }
  
  private boolean matches(IncompConstraint con) {
    return con.getInstanceName1().equalsIgnoreCase(instanceName1)
        && con.getInstanceName2().equalsIgnoreCase(instanceName2);
  }
  
  @Override
  public void applyTo(DeploymentConfiguration config) {
    ListIterator<Constraint> it = config.getConstraints().listIterator();
    while(it.hasNext()) {
      Constraint con = it.next();
      if(con instanceof IncompConstraint) {
        IncompConstraint icon = (IncompConstraint) con;
        if(this.matches(icon)) {
          // replace original constraint with dropped one
          it.remove();
          it.add(new IncompConstraint(instanceName1, instanceName2, true));
        }
      }
    }
  }
  
  public static SuggestionIncomp parseProlog(String droppedMsg, InstanceNameResolver resolver) {
    Matcher matcher = patternProlog.matcher(droppedMsg);
    if (matcher.find()) {
      String instanceName1 = resolver.resolveFromPrologName(matcher.group(1));
      String instanceName2 = resolver.resolveFromPrologName(matcher.group(2));
      return new SuggestionIncomp(instanceName1, instanceName2);
    }
    else {
      return null;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((instanceName1 == null) ? 0 : instanceName1.hashCode());
    result = prime * result + ((instanceName2 == null) ? 0 : instanceName2.hashCode());
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
    SuggestionIncomp other = (SuggestionIncomp) obj;
    if (instanceName1 == null) {
      if (other.instanceName1 != null)
        return false;
    }
    else if (!instanceName1.equals(other.instanceName1))
      return false;
    if (instanceName2 == null) {
      if (other.instanceName2 != null)
        return false;
    }
    else if (!instanceName2.equals(other.instanceName2))
      return false;
    return true;
  }
  
}
