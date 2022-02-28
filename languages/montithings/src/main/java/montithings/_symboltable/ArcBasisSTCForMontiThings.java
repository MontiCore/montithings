// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTPort;
import arcbasis._symboltable.ArcBasisScopesGenitor;
import arcbasis._symboltable.PortSymbolBuilder;
import com.google.common.base.Preconditions;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.siunittypes4math.prettyprint.SIUnitTypes4MathPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcBasisSTCForMontiThings extends ArcBasisScopesGenitor {

  TypeCheck tc;

  public ArcBasisSTCForMontiThings() {
    tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(),
      new DeriveSymTypeOfMontiThingsCombine());
  }

  public void setTypeVisitor(MCBasicTypesFullPrettyPrinter typePrinter) {
    setTypePrinter(typePrinter);
  }

  @Override
  protected String printType(@NotNull ASTMCType type) {
    assert type != null;
    if (!(type instanceof ASTSIUnitType)) {
      return type.printType(typePrinter);
    }
    return SIUnitTypes4MathPrettyPrinter.prettyprint((ASTSIUnitType) type);
  }

  // the creation of process of all symbols that use SI-Units has to be overridden
  // as SIUnits expect types to be created using the symTypeFromAST method of the
  // TypeCheck class.

  @Override
  protected VariableSymbolBuilder create_ArcField(@NotNull ASTArcField ast) {
    assert (this.getCurrentFieldType().isPresent());
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    SymTypeExpression symTypeExpression = tc.symTypeFromAST(this.getCurrentFieldType().get());
    builder.setType(symTypeExpression);
    return builder;
  }

  @Override
  public void visit(ASTArcParameter node) {
    // intentionally left empty
  }

  @Override
  public void endVisit(ASTArcParameter node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkState(this.getCurrentScope().isPresent());
    Preconditions.checkState(this.getCurrentComponent().isPresent());
    VariableSymbol symbol = this.create_ArcParameter(node).build();
    node.setSymbol(symbol);
    node.setEnclosingScope(this.getCurrentScope().get());
    symbol.setAstNode(node);
    symbol.setEnclosingScope(this.getCurrentScope().get());
    this.getCurrentScope().get().add(symbol);
    this.getCurrentComponent().get().addParameter(symbol);
  }

  @Override
  protected VariableSymbolBuilder create_ArcParameter(@NotNull ASTArcParameter ast) {
    assert (this.getCurrentScope().isPresent());
    VariableSymbolBuilder builder = ArcBasisMill.variableSymbolBuilder();
    builder.setName(ast.getName());
    SymTypeExpression symTypeExpression = tc.symTypeFromAST(ast.getMCType());
    builder.setType(symTypeExpression);
    return builder;
  }

  @Override
  protected PortSymbolBuilder create_Port(@NotNull ASTPort ast) {
    assert (this.getCurrentPortType().isPresent());
    assert (this.getCurrentPortDirection().isPresent());
    assert (this.getCurrentScope().isPresent());
    PortSymbolBuilder builder = ArcBasisMill.portSymbolBuilder();
    builder.setName(ast.getName());
    SymTypeExpression symTypeExpression = tc.symTypeFromAST(this.getCurrentPortType().get());
    builder.setType(symTypeExpression);
    builder.setDirection(this.getCurrentPortDirection().get());
    return builder;
  }
}
