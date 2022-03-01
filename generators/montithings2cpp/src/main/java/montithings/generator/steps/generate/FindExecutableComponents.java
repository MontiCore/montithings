// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static montithings.generator.helper.ComponentHelper.getInterfaceClassNames;

/**
 * Collect all the instances of the executable components (Some components
 * may only be included in other components and thus do not need an own
 * executable).
 */
public class FindExecutableComponents extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    ComponentTypeSymbol mainCompSymbol = state.getTool()
      .modelToSymbol(state.getConfig().getMainComponent(), state.getSymTab());
    List<Pair<ComponentTypeSymbol, String>> instances = ComponentHelper
      .getExecutableInstances(mainCompSymbol, state.getConfig());
    HashSet<ComponentTypeSymbol> executableComponents = new HashSet<>();
    for (Pair<ComponentTypeSymbol, String> instance : instances) {
      executableComponents.add(instance.getKey());
    }

    // Also generate code for all components that are never used directly
    // whose interface is exchanged dynamically via a port (i.e. components
    // that may be instantiated dynamically)
    for (ComponentTypeSymbol cs : getAllComponents(state.getSymTab())) {
      if (componentIsUsedDynamically(cs, state.getSymTab())) {
        executableComponents.add(cs);
      }
    }

    // Aggregate all the target folders for the components.
    List<String> executableSubdirs = new ArrayList<>(instances.size());
    for (ComponentTypeSymbol comp : executableComponents) {
      executableSubdirs.add(comp.getFullName());
    }

    state.setExecutableComponents(executableComponents);
    state.setExecutableSubdirs(executableSubdirs);
    state.setInstances(instances);
  }

  protected Set<ComponentTypeSymbol> getAllComponents(IMontiThingsGlobalScope symTab) {
    Set<ComponentTypeSymbol> allComponentTypes = new HashSet<>();
    for (IMontiThingsScope scope : symTab.getSubScopes()) {
      allComponentTypes.addAll(scope.getComponentTypeSymbols().values());
    }
    return allComponentTypes;
  }

  protected boolean componentIsUsedDynamically(ComponentTypeSymbol component,
    IMontiThingsGlobalScope symTab) {

    Set<String> namesOfImplementedInterfaces = getInterfaceClassNames(component);

    for (ComponentTypeSymbol current : getAllComponents(symTab)) {
      if (current.getPorts().stream()
        .anyMatch(p -> namesOfImplementedInterfaces.contains(p.getTypeInfo().getName()))) {
        return true;
      }
    }

    return false;
  }

}
