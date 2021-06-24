// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
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
}
