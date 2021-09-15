// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution.suggestion;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.constraint.Constraint;
import montithings.services.iot_manager.server.data.constraint.IncompConstraint;
import montithings.services.iot_manager.server.util.InstanceNameResolver;

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
  
}
