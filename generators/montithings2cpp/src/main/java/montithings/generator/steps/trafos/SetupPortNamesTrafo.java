// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.trafos;

import montithings.generator.codegen.ConfigParams;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.trafos.ComponentTypePortsNamingTrafo;

/**
 * Adds trafo that forwards ports to outermost component
 */
public class SetupPortNamesTrafo extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if (state.getConfig().getPortNameTrafo() == ConfigParams.PortNameTrafo.ON) {
      ComponentTypePortsNamingTrafo typePortsNamingTrafo = new ComponentTypePortsNamingTrafo(
        state.getConfig().getTemplatedPorts());
      state.getTool().addTrafo(typePortsNamingTrafo);
    }
  }

}
