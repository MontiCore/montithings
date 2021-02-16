package montithings.types.check;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc._symboltable.IMontiArcScope;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeExpression;
import static montithings.util.IdentifierUtils.getPortForName;

public class DeriveSymTypeOfExpression extends de.monticore.types.check.DeriveSymTypeOfExpression {

  private ExpressionsBasisVisitor realThis;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfExpression() {
    realThis = this;
  }

  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr){
    Optional<PortSymbol> optPort = getPortForName(expr);
    Optional<VariableSymbol> optVar = getScope(expr.getEnclosingScope()).resolveVariable(expr.getName());
    Optional<TypeSymbol> optType = getScope(expr.getEnclosingScope()).resolveType(expr.getName());
    if (optVar.isPresent()) {
      //no method here, test variable first
      // durch AST-Umbau kann ASTNameExpression keine Methode sein
      VariableSymbol var = optVar.get();
      SymTypeExpression res = var.getType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    } else if (optType.isPresent()) {
      //no variable found, test if name is type
      TypeSymbol type = optType.get();
      SymTypeExpression res = createTypeExpression(type.getName(), type.getEnclosingScope());
      typeCheckResult.setType();
      return Optional.of(res);
    } else if (optPort.isPresent()){
      PortSymbol port = optPort.get();
      SymTypeExpression res = port.getType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    }
    return Optional.empty();
  }


}
