/* (c) https://github.com/MontiCore/monticore */
package montithings.helper;

import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import montiarc._symboltable.PortSymbol;
import montithings.visitor.GuardExpressionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @author (last commit) kirchhof
 * @version , 13.02.2020
 * @since
 */
public class ExpressionUtil {
  public static List<PortSymbol> getPortsInGuardExpression(ASTExpression node) {
    List<PortSymbol> ports = new ArrayList<>();

    for (ASTNameExpression guardExpressionElement : getGuardExpressionElements(node)) {
      String name = guardExpressionElement.getName();
      Scope s = node.getEnclosingScopeOpt().get();
      Optional<PortSymbol> port = s.resolve(name, PortSymbol.KIND);
      port.ifPresent(ports::add);
    }
    return ports;
  }

  public static String printExpression(ASTExpression node) {
    IndentPrinter printer = new IndentPrinter();
    JavaDSLPrettyPrinter prettyPrinter = new JavaDSLPrettyPrinter(printer);
    node.accept(prettyPrinter);
    return printer.getContent();
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   *
   * @param node
   * @return
   */
  private static List<ASTNameExpression> getGuardExpressionElements(ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor);
    return visitor.getExpressions();
  }
}
