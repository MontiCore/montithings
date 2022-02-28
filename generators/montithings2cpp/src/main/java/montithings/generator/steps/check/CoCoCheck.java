// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import de.se_rwth.commons.logging.Log;
import montithings.cocos.PortConnection;
import montithings.generator.cocos.ComponentHasBehavior;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class CoCoCheck extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    Log.info("Checking models", TOOL_NAME);
    // add generator CoCos
    state.getTool().getChecker().addCoCo(new ComponentHasBehavior(state.getConfig().getHwcPath()));
    state.getTool().getChecker().addCoCo(new PortConnection(state.getConfig().getTemplatedPorts()));
    // run CoCos
    state.getTool().checkCoCos(state.getSymTab());
  }

}
