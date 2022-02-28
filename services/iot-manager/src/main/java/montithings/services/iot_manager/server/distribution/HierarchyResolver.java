// (c) https://github.com/MontiCore/monticore
package montithings.services.iot_manager.server.distribution;

import montithings.services.iot_manager.server.data.Distribution;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class is responsible for transforming instance names of instances that
 * are assigned to devices according to a hierarchy. This allows for having
 * multiple nested instances with the same "MODEL instance name" without "cross-talk"
 */
public class HierarchyResolver {

  private final Distribution distribution;
  private final List<Dependency> dependencies;

  public HierarchyResolver(Distribution dist, List<Dependency> dependencies) {
    this.distribution = dist;
    this.dependencies = dependencies;
  }

  public Distribution resolve() {
    List<Assignment> assignments = new ArrayList<>();

    // load assignments from distribution map
    for(Entry<String, String[]> a : distribution.getDistributionMap().entrySet()) {
      for(String modelInstanceName : a.getValue()) {
        assignments.add(new Assignment(a.getKey(), modelInstanceName));
      }
    }

    // Sort by clientID for deterministic behavior.
    assignments.sort((a1,a2)->a1.clientID.compareTo(a2.clientID));

    // collect all instances that are missing parents
    Set<Assignment> missingParent = new HashSet<>();
    missingParent.addAll(assignments);

    // find root instance name
    String rootInstanceName = "";
    if(assignments.size() > 0) {
      rootInstanceName = assignments.get(0).modelInstanceName;
      for(Assignment a : assignments) {
        if(rootInstanceName.startsWith(a.modelInstanceName)) {
          rootInstanceName = a.modelInstanceName;
        }
      }
    }

    // root instances do not need a parent
    for(Assignment a : assignments) {
      if(a.modelInstanceName.equals(rootInstanceName)) {
        missingParent.remove(a);
      }
    }

    // give each deployed component a unique instance name by adding a numeric
    // suffix, e.g. hierarchy.comp -> hierarchy.comp__1
    Map<String, Integer> modelInstanceCount = new HashMap<>();
    for(Assignment a : assignments) {
      int count = modelInstanceCount.getOrDefault(a.modelInstanceName, 0);
      count++;
      a.instanceName += "__" + count;
      modelInstanceCount.put(a.modelInstanceName, count);
    }

    // Sort by instance name length. Shortest instance names should be resolved
    // first, because longer instance names (potentially children) need the name
    // of the parent to already be resolved.
    assignments.sort((a1,a2)->Integer.compare(a1.modelInstanceName.length(), a2.modelInstanceName.length()));
    dependencies.sort((d1,d2)->Integer.compare(d1.dependency.modelInstanceName.length(), d2.dependency.modelInstanceName.length()));
    // apply dependencies
    for(Dependency dep : dependencies) {
      // we are only interested in dependencies that assign a child component to
      // a parent component
      if(dep.isParentRelation()) {
        // dependency is parent, dependent is child

        // find parent assignment
        Assignment parent = null;
        for(Assignment a : assignments) {
          if(a.fuzzyEquals(dep.dependency)) {
            parent = a;
          }
        }

        Assignment child = null;
        for(Assignment a : assignments) {
          if(a.fuzzyEquals(dep.dependent)) {
            child = a;
          }
        }

        adaptParentOfChild(parent, child);

        // child now has a parent
        missingParent.remove(child);
      }
    }

    // assign parents to components without parent dependency via round robin
    HashMap<String, Integer> roundRobinInstanceNumber = new HashMap<>();
    for(Assignment a : missingParent) {
      String parentModelInstanceName = getParentOf(a.modelInstanceName);
      int lastNum = roundRobinInstanceNumber.getOrDefault(parentModelInstanceName, 0);
      int num = (lastNum % modelInstanceCount.getOrDefault(parentModelInstanceName, 0)) + 1;
      roundRobinInstanceNumber.put(parentModelInstanceName, num);

      // find parent and adapt child instance name accordingly
      for(Assignment as : assignments) {
        if(as.modelInstanceName.equals(parentModelInstanceName) && as.instanceName.endsWith(String.valueOf(num))) {
          adaptParentOfChild(as, a);
          break;
        }
      }

    }

    // Now all components needing a child have a child. Thus we are done here.
    System.out.println("###########");
    for(Assignment a : assignments) {
      System.out.println(a);
    }
    System.out.println("###########");

    // reconstruct distribution (clientID -> instance names) from assignments
    Map<String, List<String>> dmap = new HashMap<>();
    for(Assignment a : assignments) {
      List<String> instanceNames = dmap.get(a.clientID);
      if(instanceNames == null) {
        instanceNames = new LinkedList<String>();
        dmap.put(a.clientID, instanceNames);
      }
      instanceNames.add(a.instanceName);
    }
    return Distribution.from(dmap);
  }

  private String getParentOf(String modelInstanceName) {
    return modelInstanceName.substring(0, modelInstanceName.lastIndexOf("."));
  }

  /**
   * Adapt the child's instance name such that {@link parent} becomes its parent.
   * */
  private void adaptParentOfChild(Assignment parent, Assignment child) {
    // instance names are of form [...].comp, thus we want to replace before the last dot.
    int lastDot = child.instanceName.lastIndexOf('.');
    if(lastDot > 0 && lastDot < child.instanceName.length() - 1) {
      child.instanceName = parent.instanceName + "." + child.instanceName.substring(lastDot + 1);
    }
  }

  public static class Assignment {
    public final String clientID;
    public final String modelInstanceName;
    public String instanceName;

    public Assignment(String clientID, String modelInstanceName) {
      this.clientID = clientID;
      this.modelInstanceName = modelInstanceName;
      this.instanceName = modelInstanceName;
    }

    @Override public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Assignment that = (Assignment) o;

      if (!Objects.equals(clientID, that.clientID))
        return false;
      if (!Objects.equals(modelInstanceName, that.modelInstanceName))
        return false;
      return Objects.equals(instanceName, that.instanceName);
    }

    /**
     * like {@code equals} but ignores {@code instanceName}
     * */
    public boolean fuzzyEquals(Assignment other) {
      return this.clientID.equals(other.clientID) && this.modelInstanceName == other.modelInstanceName;
    }

    @Override
    public int hashCode() {
      return this.clientID.hashCode() + this.modelInstanceName.hashCode();
    }

    @Override
    public String toString() {
      return "Assignment [clientID=" + clientID + ", instanceName=" + instanceName + "]";
    }

  }

  public static class Dependency {

    public final Assignment dependent;
    public final Assignment dependency;

    public Dependency(Assignment dependendent, Assignment dependency) {
      this.dependency = dependency;
      this.dependent = dependendent;
    }

    /**
     * @return whether dependency is a parent of dependent
     * */
    public boolean isParentRelation() {
      if(dependent.modelInstanceName.startsWith(dependency.modelInstanceName)) {
        // rem is the model instance name relative to its parent (starting with a dot)
        String rem = dependent.modelInstanceName.substring(dependency.modelInstanceName.length());
        long dotCount = rem.chars().filter((i)->i=='.').count();

        // if it is a direct descendant, there is only one dot.
        return dotCount == 1;
      }
      return false;
    }

    @Override
    public String toString() {
      return "Dependency [" + dependent+ " depends on " + dependency + "]";
    }

  }

}
