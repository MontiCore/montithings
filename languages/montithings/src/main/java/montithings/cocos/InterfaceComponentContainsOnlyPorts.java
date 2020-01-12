// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTPort;
import montithings._ast.ASTComponent;
import montithings._cocos.MontiThingsASTComponentCoCo;
import montithings._symboltable.ComponentSymbol;

/**
 * Interface components may only contain ports (i.e. their interface) and nothing else
 *
 * @author (last commit) kirchhof
 * @version ,
 * @since
 */
public class InterfaceComponentContainsOnlyPorts implements MontiThingsASTComponentCoCo {
  @Override public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();

    // Do not check anything if this is not an interface component
    if (!node.isInterface()) {
      return;
    }

    for (ASTElement element : node.getBody().getElementList()) {
      if (!(element instanceof ASTInterface)) {
        Log.error(
            String.format(
                "0xMT200 ASTComponent node \"%s\" is an interface component but its body " +
                    "contains an element that is not a port",
                node.getName()));
      }
    }
  }
}
