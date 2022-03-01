// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.ocl.setexpressions._ast.ASTGeneratorDeclaration;
import de.monticore.ocl.setexpressions._ast.ASTSetVariableDeclaration;
import de.monticore.ocl.setexpressions._symboltable.ISetExpressionsScope;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor2;
import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;

import java.util.List;
import java.util.Optional;

/**
 * The same as SetExpressionsSymbolTableCompleter, but typeVisitor has type
 * DeriveSymTypeOfMontiThingsCombine instead of DeriveSymTypeOfOCLCombineExpressions
 */
public class SetExpressionsSymbolTableCompleterForMontiThings
  implements SetExpressionsVisitor2, BasicSymbolsVisitor2, SetExpressionsHandler {

  DeriveSymTypeOfMontiThingsCombine typeVisitor;

  protected final List<ASTMCImportStatement> imports;

  protected final String packageDeclaration;

  protected SetExpressionsTraverser traverser;

  public void setTypeVisitor(DeriveSymTypeOfMontiThingsCombine typesCalculator) {
    if (typesCalculator != null) {
      this.typeVisitor = typesCalculator;
    }
    else {
      Log.error("0xA3201 The typesVisitor has to be set");
    }
  }

  public SetExpressionsSymbolTableCompleterForMontiThings(List<ASTMCImportStatement> imports,
    String packageDeclaration) {
    this.imports = imports;
    this.packageDeclaration = packageDeclaration;
  }

  @Override
  public SetExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SetExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ISetExpressionsScope node) {
    SetExpressionsHandler.super.traverse(node);
    for (ISetExpressionsScope subscope : node.getSubScopes()) {
      subscope.accept(this.getTraverser());
    }
  }

  @Override
  public void visit(ASTSetVariableDeclaration node) {
    // intentionally left empty
  }

  @Override
  public void endVisit(ASTSetVariableDeclaration node) {
    initialize_SetVariableDeclaration(node.getSymbol(), node);
  }

  public void initialize_SetVariableDeclaration(VariableSymbol symbol,
    ASTSetVariableDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      ast.getMCType().accept(typeVisitor.getTraverser());
      final Optional<SymTypeExpression> typeResult = typeVisitor.getResult();
      if (!typeResult.isPresent()) {
        Log.error(String
          .format("The type (%s) of the object (%s) could not be calculated", ast.getMCType(),
            ast.getName()));
      }
      else {
        symbol.setType(typeResult.get());
      }
    }
    else {
      if (ast.isPresentExpression()) {
        ast.getExpression().accept(getTraverser());
        ast.getExpression().accept(typeVisitor.getTraverser());
        if (typeVisitor.getTypeCheckResult().isPresentCurrentResult()) {
          symbol.setType(typeVisitor.getTypeCheckResult().getCurrentResult());
        }
        else {
          Log.error(
            String.format("The type of the object (%s) could not be calculated", ast.getName()));
        }
      }
      else {
        symbol
          .setType(SymTypeExpressionFactory.createTypeObject("Object", ast.getEnclosingScope()));
      }
    }
  }

  @Override
  public void endVisit(ASTGeneratorDeclaration node) {
    initialize_GeneratorDeclaration(node.getSymbol(), node);
  }

  public void initialize_GeneratorDeclaration(VariableSymbol symbol, ASTGeneratorDeclaration ast) {
    symbol.setIsReadOnly(false);
    if (ast.isPresentMCType()) {
      ast.getMCType().setEnclosingScope(symbol.getEnclosingScope());
      ast.getMCType().accept(getTraverser());
      ast.getMCType().accept(typeVisitor.getTraverser());
      final Optional<SymTypeExpression> typeResult = typeVisitor.getResult();
      if (!typeResult.isPresent()) {
        Log.error(String
          .format("The type (%s) of the object (%s) could not be calculated", ast.getMCType(),
            ast.getName()));
      }
      else {
        symbol.setType(typeResult.get());
      }
    }
    else {
      ast.getExpression().accept(typeVisitor.getTraverser());
      final Optional<SymTypeExpression> typeResult = typeVisitor.getResult();
      if (!typeResult.isPresent()) {
        Log.error(
          String.format("The type of the object (%s) could not be calculated", ast.getName()));
      }
      else if (typeResult.get().isTypeConstant()) {
        Log.error(String.format("Expression of object (%s) has to be a collection", ast.getName()));
      }
      else {
        SymTypeExpression result = OCLTypeCheck.unwrapSet(typeResult.get());
        symbol.setType(result);
      }
    }
  }
}