// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._parser.MTConfigParser;

import java.io.IOException;

public class CheckMTConfig extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    for (String model : state.getModels().getMTConfig()) {
      ASTMTConfigUnit ast = null;
      try {
        ast = new MTConfigParser().parseMTConfigUnit(model)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' MTConfig artifact was not found");
      }
      Preconditions.checkNotNull(ast);

      // parse + resolve model
      Log.info("Parsing model: " + model, "MontiThingsGeneratorTool");
      state.getConfig().setMtConfigScope(
        state.getMtConfigTool().createSymboltable(ast, state.getMtConfigGlobalScope()));

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      MTConfigCoCos.createChecker().checkAll(ast);
    }
  }

}
