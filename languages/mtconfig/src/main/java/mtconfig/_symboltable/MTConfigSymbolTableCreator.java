// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTMTConfigUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;

/**
 * Symbol table creator. Does pretty much nothing right now. Only forwards calls to MontiArc
 * that MontiCore is not advanced enough to forward automatically.
 */
public class MTConfigSymbolTableCreator extends MTConfigSymbolTableCreatorTOP {

  public MTConfigSymbolTableCreator(IMTConfigScope enclosingScope) {
    super(enclosingScope);
  }

  public MTConfigSymbolTableCreator(
      Deque<? extends IMTConfigScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public MTConfigArtifactScope createFromAST(@NotNull ASTMTConfigUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);
    if(getCurrentScope().isPresent()){
      for (IMTConfigScope scope: getCurrentScope().get().getSubScopes()) {
        for (int i = 0; i < scope.getLocalCompConfigSymbols().size(); i++) {
          if (scope.getLocalCompConfigSymbols().get(i).getAstNode().deepEquals(rootNode.getElement(i))){
            return (MTConfigArtifactScope) scope;
          }
        }
      }
    }
    MTConfigArtifactScope artifactScope = mtconfig.MTConfigMill.mTConfigArtifactScopeBuilder()
        .setPackageName(rootNode.getPackage().getQName())
        .setImportList(new ArrayList<>())
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }

  @Override
  public void visit(ASTMTConfigUnit node) {
    if (getCurrentScope().isPresent()) {
      node.setEnclosingScope(getCurrentScope().get());
    }
    else {
      Log.error("Could not set enclosing scope of ASTNode \"" + node
          + "\", because no scope is set yet!");
    }
    mtconfig._symboltable.IMTConfigScope scope = create_MTConfigUnit(node);
    initialize_MTConfigUnit(scope, node);
    //putOnStack(scope);
    setLinkBetweenSpannedScopeAndNode(scope, node);
  }

  @Override
  public void endVisit(ASTMTConfigUnit node) {
  }

  protected  mtconfig._symboltable.CompConfigSymbol create_CompConfig (mtconfig._ast.ASTCompConfig ast)  {
    return mtconfig.MTConfigMill.compConfigSymbolBuilder().setName(ast.getName()+"_"+ast.getPlatform()).build();
  }
}
