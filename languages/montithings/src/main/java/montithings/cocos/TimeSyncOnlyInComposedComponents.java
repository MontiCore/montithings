// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTStereotype;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * Checks that the timesync stereotype is only used in composed components,
 * since a single component running in timesync mode is undefined behavior
 * and probably an error
 *
 * @author (last commit)
 */
public class TimeSyncOnlyInComposedComponents implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
              String.format("0xMT010 ASTComponent node \"%s\" has no " +
                              "symbol. Did you forget to run the " +
                              "SymbolTableCreator before checking cocos?",
                      node.getName()));
      return;
    }
    if (node.getStereotypeOpt().isPresent()){
      ASTStereotype stereotype = node.getStereotypeOpt().get();
      if (stereotype.containsStereoValue("timesync")){
        ComponentSymbol symbol = (ComponentSymbol) node.getSymbolOpt().get();
        if (symbol.isAtomic()){
          Log.error("0xMT119 The timesync stereotype should only be used in composed components",
                  node.get_SourcePositionStart());
        }
      }
    }
  }
}
