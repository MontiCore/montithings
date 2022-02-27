// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import bindings._ast.ASTBindingRule;
import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCos;
import bindings._parser.BindingsParser;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.IOException;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

/**
 * Check Cocos of Binding models
 */
public class CheckBindings extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    for (String binding : state.getModels().getBindings()) {
      ASTBindingsCompilationUnit bindingsAST = null;
      try {
        bindingsAST = new BindingsParser().parseBindingsCompilationUnit(binding)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + binding));
      }
      catch (IOException e) {
        Log.error("File '" + binding + "' Bindings artifact was not found");
      }
      Preconditions.checkNotNull(bindingsAST);
      Log.info("Parsing model: " + binding, TOOL_NAME);
      state.getBindingsTool().createSymboltable(bindingsAST, state.getBinTab());

      Log.info("Check Binding: " + binding, "MontiArcGeneratorTool");
      BindingsCoCos.createChecker().checkAll(bindingsAST);

      for (bindings._ast.ASTElement rule : bindingsAST.getElementList()) {
        if (rule instanceof ASTBindingRule) {
          state.getConfig().getComponentBindings().add((ASTBindingRule) rule);
        }
      }
    }
  }

}
