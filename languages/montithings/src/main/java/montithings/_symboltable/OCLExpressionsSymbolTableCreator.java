package montithings._symboltable;

import de.monticore.ocl.oclexpressions._ast.ASTInDeclaration;
import de.monticore.ocl.oclexpressions._ast.ASTInDeclarationVariable;
import de.monticore.ocl.oclexpressions._ast.ASTOCLVariableDeclaration;
import de.monticore.ocl.oclexpressions._symboltable.IOCLExpressionsScope;
import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCreatorTOP;
import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.Deque;

public class OCLExpressionsSymbolTableCreator extends OCLExpressionsSymbolTableCreatorTOP {

  private TypeCheck typeVisitor;

  public OCLExpressionsSymbolTableCreator(){
    super();
    typeVisitor = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public OCLExpressionsSymbolTableCreator(IOCLExpressionsScope enclosingScope) {
    super(enclosingScope);
    typeVisitor = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public OCLExpressionsSymbolTableCreator(Deque<? extends IOCLExpressionsScope> scopeStack) {
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
  public void visit(ASTOCLVariableDeclaration node){

  }

  @Override
  public void endVisit(ASTOCLVariableDeclaration node){
    VariableSymbol symbol = create_OCLVariableDeclaration(node);
    if(getCurrentScope().isPresent()){
      symbol.setEnclosingScope(getCurrentScope().get());
    }
    addToScopeAndLinkWithNode(symbol, node);
    initialize_OCLVariableDeclaration(symbol, node);
  }

  @Override
  public void initialize_OCLVariableDeclaration(VariableSymbol symbol, ASTOCLVariableDeclaration ast) {
    symbol.setIsReadOnly(false);
    if(ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getRealThis());
      final SymTypeExpression result = typeVisitor.symTypeFromAST(ast.getMCType());
      symbol.setType(result);
    } else {
      if(ast.isPresentExpression()){
        SymTypeExpression result = typeVisitor.typeOf(ast.getExpression());
        symbol.setType(result);
      }
      else {
        symbol.setType(SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
      }
    }
  }

  @Override
  public void visit(ASTInDeclaration node){

  }

  @Override
  public void endVisit(ASTInDeclaration node){
    for(int i = 0; i < node.getInDeclarationVariableList().size(); i++){
      VariableSymbol symbol = create_InDeclarationVariable(node.getInDeclarationVariable(i));
      if(getCurrentScope().isPresent()){
        symbol.setEnclosingScope(getCurrentScope().get());
      }
      addToScopeAndLinkWithNode(symbol, node.getInDeclarationVariable(i));
      initialize_InDeclarationVariable(symbol, node);
    }
  }

  @Override
  public void visit(ASTInDeclarationVariable node){

  }

  @Override
  public void endVisit(ASTInDeclarationVariable node){

  }

  @Override
  public void initialize_InDeclarationVariable(VariableSymbol symbol, ASTInDeclarationVariable ast){

  }

  public void initialize_InDeclarationVariable(VariableSymbol symbol, ASTInDeclaration ast) {
    symbol.setIsReadOnly(false);
    SymTypeExpression typeResult = null;
    if(ast.isPresentMCType()){
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getRealThis());
      typeResult = typeVisitor.symTypeFromAST(ast.getMCType());
      symbol.setType(typeResult);
    }
    if(ast.isPresentExpression()){
      SymTypeExpression result = typeVisitor.typeOf(ast.getExpression());
      //if MCType present: check that type of expression and MCType are compatible
      if(ast.isPresentMCType() && !OCLTypeCheck.compatible(typeResult,
                OCLTypeCheck.unwrapSet(result))){
        Log.error(String.format("The MCType (%s) and the expression type (%s) in Symbol (%s) are not compatible",
                  ast.getMCType(), OCLTypeCheck.unwrapSet(result), symbol.getName()));
      }
      //if no MCType present: symbol has type of expression
      if(!ast.isPresentMCType()){
        symbol.setType(OCLTypeCheck.unwrapSet(result));
      }
    }
    //node has neither MCType nor expression
    if(!ast.isPresentMCType() && !ast.isPresentExpression()) {
      symbol.setType(SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
    }
  }
}