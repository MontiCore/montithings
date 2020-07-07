// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import arcbasis._ast.ASTComponentType;
import montithings._ast.ASTMTComponentType;

import java.util.Deque;

/**
 * Symbol table creator. Does pretty much nothing right now. Only forwards calls to MontiArc
 * that MontiCore is not advanced enough to forward automatically.
 */
public class MontiThingsSymbolTableCreator extends MontiThingsSymbolTableCreatorTOP {

  public MontiThingsSymbolTableCreator(IMontiThingsScope enclosingScope) {
    super(enclosingScope);
  }

  public MontiThingsSymbolTableCreator(
      Deque<? extends IMontiThingsScope> scopeStack) {
    super(scopeStack);
  }

  @Override public void visit(ASTMTComponentType node) {
    getRealThis().visit((ASTComponentType) node);
  }

  @Override public void endVisit(ASTMTComponentType node) {
    getRealThis().endVisit((ASTComponentType) node);
  }
}
