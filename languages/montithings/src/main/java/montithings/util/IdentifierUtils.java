// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;

import java.util.Optional;

public class IdentifierUtils {

  // Util class - prevent instantiation
  private IdentifierUtils() {
  }

  /**
   * Finds the Port for a NameExpression
   *
   * @param node the node to search the portsymbol for
   * @return PortSymbol if found, Optional.empty if name is not inside an IMontiArcScope
   */
  public static Optional<PortSymbol> getPortForName(ASTNameExpression node) {
    if (!(node.getEnclosingScope() instanceof IMontiArcScope)) {
      return Optional.empty();
    }
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    String name = node.getName();
    return s.resolvePort(name);
  }

  /**
   * Resolves a ComponentTypeSymbolSurrogate to a ComponentTypeSymbol
   *
   * @param comp the ComponentTypeSymbolSurrogate which should be resolved
   * @return ComponentTypeSymbol which was resolved
   */
  public static ComponentTypeSymbol resolveComponentTypeSymbolSurrogate(ComponentTypeSymbol comp) {
    Preconditions.checkArgument(comp != null);

    ComponentTypeSymbol curSym = comp;
    while (curSym instanceof ComponentTypeSymbolSurrogate) {
      ComponentTypeSymbolSurrogate surrogate = (ComponentTypeSymbolSurrogate) curSym;
      ComponentTypeSymbol updatedSym = surrogate.lazyLoadDelegate();
      if (updatedSym == surrogate) {
        Log.error(String.format("Component type '%s' cannot be resolved. The corresponding component type " +
          "surrogate does not lead anywhere.", comp.getFullName()
        ));
      }
      curSym = updatedSym;
    }
    return curSym;
  }
}
