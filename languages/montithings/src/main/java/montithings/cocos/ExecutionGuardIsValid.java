/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.monticore.java.javadsl._ast.ASTIfStatement;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montithings._ast.ASTExecutionBlock;
import montithings._ast.ASTExecutionIfStatement;
import montithings.visitor.GuardExpressionVisitor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author (last commit) JFuerste
 */
public class ExecutionGuardIsValid  implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    if (!node.getSpannedScopeOpt().isPresent()){
      Log.error(
              String.format("0xMT020 ASTComponent node \"%s\" has no " +
                              "spanned scope. Did you forget to run the " +
                              "SymbolTableCreator before checking cocos?",
                      node.getName()));
      return;
    }
    Scope s = node.getSpannedScopeOpt().get();
    for (ASTExecutionIfStatement ifStatement : getIfStatements(node)) {
      GuardExpressionVisitor visitor = new GuardExpressionVisitor();
      ifStatement.accept(visitor);

      for (ASTNameExpression expression : visitor.getExpressions()) {
        String name = expression.getName();
        Optional<PortSymbol> port = s.resolve(name, PortSymbol.KIND);
        Optional<VariableSymbol> var = s.resolve(name, VariableSymbol.KIND);
        Optional<JavaTypeSymbol> jType = s.resolve(name, JavaTypeSymbol.KIND);
        if (!port.isPresent() && !var.isPresent() && !jType.isPresent()){
          Log.error("0xMT108 The variable " + name + " is not defined in this scope" ,
                  ifStatement.get_SourcePositionStart());
        }
      }

    }

  }

  private List<ASTExecutionIfStatement> getIfStatements(ASTComponent node){
    return node.getBody().getElementList()
            .stream()
            .filter(e -> e instanceof ASTExecutionBlock)
            .flatMap(e -> ((ASTExecutionBlock) e).getExecutionStatementList().stream())
            .filter(e -> e instanceof ASTExecutionIfStatement)
            .map(e -> (ASTExecutionIfStatement) e)
            .collect(Collectors.toList());
  }
}
