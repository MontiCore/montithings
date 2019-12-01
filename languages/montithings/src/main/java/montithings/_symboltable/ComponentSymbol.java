/* (c) https://github.com/MontiCore/monticore */
package montithings._symboltable;

import de.monticore.symboltable.SymbolKind;
import montithings._ast.ASTComponent;

/**
 * TODO
 *
 * @author (last commit) kirchhof
 */
public class ComponentSymbol extends montiarc._symboltable.ComponentSymbol {
  public ComponentSymbol(String name) {
    super(name);
  }

  public ComponentSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  public boolean isInterfaceComponent() {
    if (!(getAstNode().isPresent() || getAstNode().get() instanceof ASTComponent)) {
      return false;
    }
    ASTComponent component = (ASTComponent) getAstNode().get();
    return component.isInterface();
  }
}
