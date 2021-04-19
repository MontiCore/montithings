// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.check.SymTypeExpression;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeExpression;
import static montithings.util.IdentifierUtils.getPortForName;

public class DeriveSymTypeOfExpressionForMT extends de.monticore.types.check.DeriveSymTypeOfExpression {

  private ExpressionsBasisVisitor realThis;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfExpressionForMT() {
    realThis = this;
  }

  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(ASTNameExpression expr){
    IBasicSymbolsScope scope = getScope(expr.getEnclosingScope());
    Optional<PortSymbol> optPort = getPortForName(expr);
    Optional<VariableSymbol> optVar = scope.resolveVariable(expr.getName());
    Optional<TypeSymbol> optType = scope.resolveType(expr.getName());
    Optional<FunctionSymbol> optFunction = scope.resolveFunction(expr.getName());
    Optional<FieldSymbol> optField = Optional.empty();
    if(scope instanceof IOOSymbolsScope){
      optField = ((IOOSymbolsScope) scope).resolveField(expr.getName());
    }
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
    } else if (optPort.isPresent()) {
      PortSymbol port = optPort.get();
      SymTypeExpression res = port.getType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    } else if (optFunction.isPresent()) {
      FunctionSymbol function = optFunction.get();
      SymTypeExpression res = function.getReturnType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    } else if (optField.isPresent()) {
      FieldSymbol field = optField.get();
      SymTypeExpression res = field.getType().deepClone();
      typeCheckResult.setField();
      return Optional.of(res);
    }
    return Optional.empty();
  }
}
