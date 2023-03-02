// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import mtconfig.MTConfigMill;
import mtconfig._ast.ASTCompConfig;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCos;
import montiarc._ast.ASTMACompilationUnit;

import java.io.IOException;

public class CheckMTConfig extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    MTConfigMill.init();

    for (String model : state.getModels().getMTConfig()) {
      ASTMTConfigUnit ast = null;
      try {
        ast = MTConfigMill.parser().parseMTConfigUnit(model)
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

    for (ASTMACompilationUnit c : state.getNotSplittedComponents()) {
      ASTCompConfig cfg = MTConfigMill
        .compConfigBuilder()
        .setComponentType(
          c.getComponentType().getName()
        )
        .setPlatform("GENERIC")
        .addMTCFGTag(
          MTConfigMill.separationHintBuilder().build()
        )
        .build();

      ASTMTConfigUnit cu = MTConfigMill
        .mTConfigUnitBuilder()
        .setPackage(
          MTConfigMill.mCQualifiedNameBuilder().addParts(c.getPackage().getQName()).build()
        )
        .addElement(cfg)
        .build();

      state.getConfig().setMtConfigScope(
        state.getMtConfigTool().createSymboltable(cu, state.getMtConfigGlobalScope())
      );
    }

    MTConfigMill.reset();
    BasicSymbolsMill.initializePrimitives();
  }

}
