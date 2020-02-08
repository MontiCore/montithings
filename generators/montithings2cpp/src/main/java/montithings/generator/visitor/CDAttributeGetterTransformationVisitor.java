// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.mcexpressions._ast.ASTArguments;
import de.monticore.mcexpressions._ast.ASTCallExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc._symboltable.adapters.CDTypeSymbol2JavaType;
import montithings._symboltable.SyncStatementSymbol;
import montithings.generator.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transforms cd attribute calls to getter expression which correspond to the
 * generated Java class methods
 *
 * @author Pfeiffer
 * @version $Revision$, $Date$
 */
public class CDAttributeGetterTransformationVisitor extends JavaDSLPrettyPrinter {

  /**
   * indicates whether the current visited node is a attribute call expression
   */
  boolean isAttributeCall = true;

  /**
   * Constructor for
   * montithings.generator.visitor.MontiArcJavaDSLPrettyPrinter
   *
   * @param out
   * @param left
   */
  public CDAttributeGetterTransformationVisitor(IndentPrinter out) {
    super(out);
  }

  /**
   * @see de.monticore.expressions.prettyprint.MCExpressionsPrettyPrinter#handle(de.monticore.mcexpressions._ast.ASTCallExpression)
   */
  @Override
  public void handle(ASTCallExpression node) {
    isAttributeCall = false;
    super.handle(node);
  }

  /**
   * @see de.monticore.expressions.prettyprint.MCExpressionsPrettyPrinter#handle(de.monticore.mcexpressions._ast.ASTArguments)
   */
  @Override
  public void handle(ASTArguments a) {
    isAttributeCall = true;
    super.handle(a);
  }

  /**
   * @see de.monticore.java.expressions._visitor.ExpressionsVisitor#handle(de.monticore.java.expressions._ast.ASTQualifiedNameExpression)
   */
  @Override
  public void handle(ASTQualifiedNameExpression node) {
    Scope s = node.getEnclosingScopeOpt().get();

    String attrCall = node.getName();

    // checks whether the calling name is an Enum type
    if (node.getExpression() instanceof ASTNameExpression) {
      String callingName = ((ASTNameExpression) node.getExpression()).getName();
      Optional<JTypeSymbol> typeOpt = getTypeFromName(callingName, s);
      if (typeOpt.isPresent()) {
        JTypeSymbol type = typeOpt.get();
        if (type instanceof CDTypeSymbol2JavaType) {
          if (((CDTypeSymbol2JavaType) type).getAdaptee().isEnum()) {
            isAttributeCall = false;
          }
        }
        else {
          isAttributeCall = false;
        }
      }
    }
    // checks whether the name calling is a full qualified type name
    else if (node.getExpression() instanceof ASTQualifiedNameExpression) {
      String name = ((ASTQualifiedNameExpression) node.getExpression()).getName();
      Optional<JTypeSymbol> typeOfName = getTypeFromName(name, s);
      if (typeOfName.isPresent() && !(typeOfName.get() instanceof CDTypeSymbol2JavaType)) {
        isAttributeCall = false;
      }
    }

    if (isAttributeCall) {
      attrCall = "get" + Character.toUpperCase(attrCall.charAt(0)) + attrCall.substring(1) + "()";
    }

    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getExpression().accept(getRealThis());
    getPrinter().print(".");
    printNode(attrCall);
    CommentPrettyPrinter.printPostComments(node, getPrinter());
    isAttributeCall = true;
  }

  private Optional<JTypeSymbol> getTypeFromName(String name, Scope s) {
    Optional<PortSymbol> port = s.resolve(name, PortSymbol.KIND);
    Optional<VariableSymbol> var = s.resolve(name, VariableSymbol.KIND);
    Optional<JavaTypeSymbol> jType = s.resolve(name, JavaTypeSymbol.KIND);
    Optional<JTypeSymbol> typeOpt = Optional.empty();
    if (port.isPresent()) {
      typeOpt = Optional.of(port.get().getTypeReference().getReferencedSymbol());
    }
    else if (var.isPresent()) {
      typeOpt = Optional.of(var.get().getTypeReference().getReferencedSymbol());
    }
    else if (jType.isPresent()) {
      typeOpt = Optional.of(jType.get());
    }

    return typeOpt;
  }

  @Override
  public void handle(ASTNameExpression node) {
    Scope s = node.getEnclosingScopeOpt().get();
    String name = node.getName();
    Optional<PortSymbol> port = s.resolve(name, PortSymbol.KIND);
    Optional<SyncStatementSymbol> sync = s.resolve(name, SyncStatementSymbol.KIND);

    ComponentSymbol comp = new ComponentSymbol("", ComponentSymbol.KIND);
    if (s.getSpanningSymbol().get() instanceof ComponentSymbol) {
      // If-then-else expression
      comp = (ComponentSymbol) s.getSpanningSymbol().get();
    } else if (s.getSpanningSymbol().get() instanceof TransitionSymbol) {
      // Automaton expression
      comp = (ComponentSymbol) s.getSpanningSymbol().get().getEnclosingScope().getEnclosingScope().get().getSpanningSymbol().get();
    } else {
      Log.error("ASTNameExpression " + node.getName() + "has an unknown scope (neither if-then-else nor automaton)");
    }
    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);

    if (port.isPresent()) {
      printer.print("input.get" + capitalize(node.getName()) + "().value()");
    }
    else if (sync.isPresent()) {
      String synced = "(";
      String s1 = sync.get().
          getSyncStatementNode()
          .get()
          .getSyncedPortList()
          .stream()
          .map(str -> "input.get" + capitalize(str) + "()" + isSet(portsInBatchStatement, str))
          .collect(Collectors.joining(" && "));
      synced += s1;
      synced += ")";
      printer.print(synced);

    }
    else {
      printNode(node.getName());
    }
  }

  private String capitalize(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private String isSet(List<PortSymbol> batchPorts, String name) {
    return batchPorts.stream()
        .filter(p -> p.getName().equals(name))
        .findFirst()
        .map(p -> ".size() > 0")
        .orElse("");
  }

}
