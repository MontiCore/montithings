// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.data.constraint.processor;

import montithings.services.iot_manager.server.data.DeploymentConfiguration;
import montithings.services.iot_manager.server.data.constraint.BasicConstraint;
import montithings.services.iot_manager.server.data.constraint.Constraint;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BasicConstraintInstanceWildcardProcessor implements ConstraintProcessor {
  
  @Override
  public void apply(ConstraintContext context, DeploymentConfiguration target) {
    ListIterator<Constraint> it = target.getConstraints().listIterator();
    
    while (it.hasNext()) {
      Constraint constraint = it.next();
      if (constraint instanceof BasicConstraint) {
        BasicConstraint bcon = (BasicConstraint) constraint;
        
        if (containsWildcard(bcon.getInstanceSelector())) {
          // This constraint contains a wildcard. Remove it and add constraint
          // for every fitting real location.
          it.remove();
          for (String instanceName : getMatchingInstanceNames(context, bcon.getInstanceSelector())) {
            // construct new constraint
            BasicConstraint newCon = bcon.clone();
            newCon.setInstanceSelector(instanceName);
            // add it to the constraint list
            it.add(newCon);
          }
        }
      }
    }
  }
  
  private boolean containsWildcard(String instanceSelector) {
    return instanceSelector.contains("*");
  }
  
  /**
   * @return All matching instance names. instanceSelector matches instanceName
   *         when {@code instanceSelector = instanceName} or for
   *         {@code instanceSelector = "a.*.c"} and
   *         {@code instanceName = "a.b.c"}
   */
  private List<String> getMatchingInstanceNames(ConstraintContext ctx, String instanceSelector) {
    // Construct regular expression to test for match
    String[] split = instanceSelector.split(Pattern.quote("*"));
    List<String> quoted = Arrays.stream(split).map(Pattern::quote).collect(Collectors.toList());
    String patternStr = String.join(".+", quoted);
    Pattern pat = Pattern.compile(patternStr);
    Predicate<String> matches = pat.asPredicate();
    
    // Collect all matching instance names.
    return ctx.getConfig().getDeploymentInfo().getInstanceNames().stream()
      .filter(matches)
      .collect(Collectors.toList());
  }
  
  @Override
  public void clean(ConstraintContext context, DeploymentConfiguration target) {
    
  }
  
}
