// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTStereotype;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montithings._ast.ASTCalculationInterval;
import montithings._ast.ASTControlBlock;

/**
 * Gives a warning if a timesync component contains subcomponents that use timing options
 *
 * @author (last commit)
 */
public class TimeSyncInSubComponents implements MontiArcASTComponentCoCo {

  String topCompName = "";

  @Override
  public void check(ASTComponent node) {
    topCompName = node.getName();
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMT010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }

    if (node.getStereotypeOpt().isPresent()) {
      ASTStereotype stereotype = node.getStereotypeOpt().get();
      if (stereotype.containsStereoValue("timesync")) {
        ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();

        for (ComponentInstanceSymbol subComponentInstance : comp.getSubComponents()) {
          ComponentSymbol subComponent = subComponentInstance.getComponentType()
              .getReferencedSymbol();
          checkSubComponentsForTimingOptions(subComponent);
        }

      }
    }
  }

  private void checkSubComponentsForTimingOptions(ComponentSymbol comp) {
    ASTComponent node = (ASTComponent) comp.getAstNode().get();

    //Check for Timesync stereotype
    if (node.getStereotypeOpt().isPresent()) {
      ASTStereotype stereotype = node.getStereotypeOpt().get();
      if (stereotype.containsStereoValue("timesync")) {
        Log.warn("0xMT120 Timesynced component " + topCompName + " contains " +
                "subcomponent " + node.getName() + " with timing options.",
            stereotype.get_SourcePositionStart());
      }
    }

    //Check for Update interval
    node.getBody().getElementList()
        .stream()
        .filter(ASTControlBlock.class::isInstance)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(ASTCalculationInterval.class::isInstance)
        .forEach(e ->
            Log.warn("0xMT120 Timesynced component " + topCompName + " contains " +
                    "subcomponent " + node.getName() + " with timing options.",
                e.get_SourcePositionStart()));
  }

}