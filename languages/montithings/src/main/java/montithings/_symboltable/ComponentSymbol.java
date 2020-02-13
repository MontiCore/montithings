// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import montiarc._symboltable.PortSymbol;
import montithings._ast.ASTAssumption;
import montithings._ast.ASTComponent;
import montithings._ast.ASTExecutionIfStatement;
import montithings._ast.ASTGuarantee;
import montithings.visitor.ExpressionEnclosingScopeSetterVisitor;
import montithings.visitor.GuardExpressionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  private boolean isTimeSync = getStereotype().containsKey("timesync");

  public boolean isInterfaceComponent() {
    if (!(getAstNode().isPresent() || getAstNode().get() instanceof ASTComponent)) {
      return false;
    }
    ASTComponent component = (ASTComponent) getAstNode().get();
    return component.isInterface();
  }

  public boolean isTimeSync() {
    return isTimeSync;
  }

  public List<ASTAssumption> getAssumptions() {
    List<ASTAssumption> list =  ((ASTComponent)getAstNode().get())
        .getBody()
        .getElementList().stream()
        .filter(e -> e instanceof ASTAssumption)
        .map(e -> ((ASTAssumption) e))
        .collect(Collectors.toList());
    return list;
  }

  public List<ASTGuarantee> getGuarantees() {
    List<ASTGuarantee> list =  ((ASTComponent)getAstNode().get())
        .getBody()
        .getElementList().stream()
        .filter(e -> e instanceof ASTGuarantee)
        .map(e -> ((ASTGuarantee) e))
        .collect(Collectors.toList());
    return list;
  }


}
