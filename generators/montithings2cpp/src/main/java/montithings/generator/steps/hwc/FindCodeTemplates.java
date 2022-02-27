// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.hwc;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.generator.visitor.FindTemplatedPortsVisitor;
import montithings.generator.visitor.GenericInstantiationVisitor;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

/**
 * Looks for FTL files in HWC
 */
public class FindCodeTemplates extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    Log.info("Looking for code templates", TOOL_NAME);

    GenericInstantiationVisitor genericInstantiationVisitor = new GenericInstantiationVisitor();

    for (String model : state.getModels().getMontithings()) {
      // Parse model
      String qualifier = Names.getQualifier(model);
      String qualifiedModelName = qualifier + (qualifier.isEmpty() ? "" : ".")
        + Names.getSimpleName(model);
      ComponentTypeSymbol comp = state.getSymTab().resolveComponentType(qualifiedModelName).get();

      Log.info("Searching templates for: " + comp.getFullName(), TOOL_NAME);

      // Find ports with templates
      FindTemplatedPortsVisitor visitor = new FindTemplatedPortsVisitor(state.getConfig());
      comp.getAstNode().accept(visitor.createTraverser());
      state.getConfig().getTemplatedPorts().addAll(visitor.getTemplatedPorts());

      comp.getAstNode().accept(genericInstantiationVisitor.createTraverser());
    }

    state.getConfig().setTypeArguments(genericInstantiationVisitor.getTypeArguments());
  }

}
