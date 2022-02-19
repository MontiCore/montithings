// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.generator.prettyprinter.CppPrettyPrinterUtils.*;
import static montithings.util.IdentifierUtils.getPortForName;

/**
 * Transforms cd attribute calls to getter expression which correspond to the
 * generated C++ class methods
 */
public class CppExpressionPrettyPrinter extends ExpressionsBasisPrettyPrinter {

  public CppExpressionPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  @Override
  public void handle(ASTNameExpression node) {
    handle(node, false);
  }

  protected void handle(ASTNameExpression node, boolean isComparedToNoData) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node);
    if (!port.isPresent()) {
      // Check if this is an Enum Constant from CD4a
      Optional<FieldSymbol> symbol = ((IMontiThingsScope) node.getEnclosingScope()).resolveField(node.getName());
      boolean isEnum = symbol.isPresent() && symbol.get().getAstNode() instanceof ASTCDEnumConstant;

      if (symbol.isPresent() && isEnum) {
        String fullName = symbol.get().getFullName();
        String cppFullyQualifiedName = fullName.replaceAll("\\.", "::");
        getPrinter().print("montithings::" + cppFullyQualifiedName);
      } else {
        if (isStateVariable(node)) {
          getPrinter().print(Identifier.getStateName() + ".get");
          getPrinter().print(capitalize(node.getName()) + "()");
        } else {
          getPrinter().print(node.getName());
        }
      }
      return;
    }

    IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
    ComponentTypeSymbol comp = (ComponentTypeSymbol) componentScope.getSpanningSymbol();

    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);
    List<ASTSyncStatement> syncStatements = ComponentHelper
      .elementsOf(comp).filter(ASTSyncStatement.class::isInstance).map(ASTSyncStatement.class::cast)
      .collect(Collectors.toList());

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      if (isComparedToNoData || portsInBatchStatement.contains(port.get())) {
        getPrinter().print(prefix + ".get" + capitalize(node.getName()) + "()");
      }
      else {
        getPrinter().print(prefix + ".get" + capitalize(node.getName()) + "().value()");
      }
    }
    else if (!syncStatements.isEmpty()) {
      StringBuilder synced = new StringBuilder("(");
      for (ASTSyncStatement sync : syncStatements) {
        String s1 = sync
          .getSyncedPortList()
          .stream()
          .map(str -> Identifier.getInputName() + ".get" + capitalize(str) + "()" + isSet(portsInBatchStatement, str))
          .collect(Collectors.joining(" && "));
        synced.append(s1);
      }
      synced.append(")");
      getPrinter().print(synced.toString());

    }
    else {
      if (isStateVariable(node)) {
        getPrinter().print(Identifier.getStateName() + ".");
      }
      getPrinter().print(node.getName());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
