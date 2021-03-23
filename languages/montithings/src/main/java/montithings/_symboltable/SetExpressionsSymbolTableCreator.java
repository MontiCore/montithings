package montithings._symboltable;

import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetVariableDeclaration;
import de.monticore.ocl.setexpressions._symboltable.ISetExpressionsScope;
import de.monticore.ocl.setexpressions._symboltable.SetExpressionsSymbolTableCreatorTOP;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLCombineExpressions;
import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.Deque;
import java.util.Optional;

public class SetExpressionsSymbolTableCreator extends SetExpressionsSymbolTableCreatorTOP {
  private TypeCheck typeVisitor;

  public SetExpressionsSymbolTableCreator(){
    super();
    typeVisitor = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public SetExpressionsSymbolTableCreator(ISetExpressionsScope enclosingScope) {
    super(enclosingScope);
    typeVisitor = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public SetExpressionsSymbolTableCreator(Deque<? extends ISetExpressionsScope> scopeStack) {
    super(scopeStack);
    typeVisitor = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public void setTypeVisitor(TypeCheck typesCalculator) {
    if (typesCalculator != null) {
      this.typeVisitor = typesCalculator;
    }
    else {
      Log.error("0xA3201 The typesVisitor has to be set");
    }
  }

  @Override
  public void visit(ASTSetVariableDeclaration node){

  }

  @Override
  public void endVisit(ASTSetVariableDeclaration node){
    VariableSymbol symbol = create_SetVariableDeclaration(node);
    if(getCurrentScope().isPresent()){
      symbol.setEnclosingScope(getCurrentScope().get());
    }
    addToScopeAndLinkWithNode(symbol, node);
    initialize_SetVariableDeclaration(symbol, node);
  }

  @Override
  public void initialize_SetVariableDeclaration(VariableSymbol symbol, ASTSetVariableDeclaration ast) {
    symbol.setIsReadOnly(false);
    if(ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getRealThis());
      final SymTypeExpression typeResult = typeVisitor.symTypeFromAST(ast.getMCType());
      symbol.setType(typeResult);
    } else {
      if(ast.isPresentExpression()){
        ast.getExpression().accept(getRealThis());
        SymTypeExpression result = typeVisitor.typeOf(ast.getExpression());
        symbol.setType(result);
      }
      else {
        symbol.setType(SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
      }
    }
  }

  @Override
  public void visit(ASTGeneratorDeclaration node){

  }

  @Override
  public void endVisit(ASTGeneratorDeclaration node){
    VariableSymbol symbol = create_GeneratorDeclaration(node);
    if (getCurrentScope().isPresent()) {
      symbol.setEnclosingScope(getCurrentScope().get());
    }
    addToScopeAndLinkWithNode(symbol, node);
    initialize_GeneratorDeclaration(symbol, node);
  }

  @Override
  public void initialize_GeneratorDeclaration(VariableSymbol symbol, ASTGeneratorDeclaration ast) {
    symbol.setIsReadOnly(false);
    if(ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getRealThis());
      final SymTypeExpression typeResult = typeVisitor.symTypeFromAST(ast.getMCType());
      symbol.setType(typeResult);
    } else {
      final SymTypeExpression typeResult = typeVisitor.typeOf(ast.getExpression());
      if(typeResult.isTypeConstant()){
        Log.error(String.format("Expression of object (%s) has to be a collection", ast.getName()));
      }
      else {
        SymTypeExpression result = OCLTypeCheck.unwrapSet(typeResult);
        symbol.setType(result);
      }
    }
  }
}
