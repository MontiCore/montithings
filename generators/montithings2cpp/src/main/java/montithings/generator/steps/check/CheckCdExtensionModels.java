// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension._symboltable.ICDLangExtensionGlobalScope;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.IOException;
import java.util.List;

public class CheckCdExtensionModels extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    List<String> foundCDExtensionModels = state.getModels().getCdextensions();

    for (String model : foundCDExtensionModels) {
      ASTCDLangExtensionUnit cdExtensionAST = null;
      try {
        cdExtensionAST = new CDLangExtensionParser().parseCDLangExtensionUnit(model)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' CDLangExtension artifact was not found");
      }
      Preconditions.checkNotNull(cdExtensionAST);

      // parse + resolve model
      Log.info("Parsing model: " + model, "MontiThingsGeneratorTool");
      if (state.getConfig().getCdLangExtensionScope() == null) {
        state.getConfig()
          .setCdLangExtensionScope(
            state.getCdExtensionTool().createSymboltable(cdExtensionAST, state.getModelPath()));
      }
      else {
        state.getCdExtensionTool().createSymboltable(cdExtensionAST,
          (ICDLangExtensionGlobalScope) state.getConfig().getCdLangExtensionScope());
      }

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      CDLangExtensionCoCos.createChecker().checkAll(cdExtensionAST);
    }
  }

}
