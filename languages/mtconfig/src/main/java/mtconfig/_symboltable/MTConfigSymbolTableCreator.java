// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTMTConfigUnit;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Deque;

/**
 * Symbol table creator.
 */
public class MTConfigSymbolTableCreator extends MTConfigSymbolTableCreatorTOP {

  public MTConfigSymbolTableCreator(IMTConfigScope enclosingScope) {
    super(enclosingScope);
  }

  public MTConfigSymbolTableCreator(
      Deque<? extends IMTConfigScope> scopeStack) {
    super(scopeStack);
  }

  /**
   * Creates MTConfigArtifactScope from ast.
   * The package is set in the ArtifactScope.
   * @param rootNode AST root used for creation.
   * @return scope created from given AST.
   */
  @Override
  public IMTConfigArtifactScope createFromAST(@NotNull ASTMTConfigUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);

    IMTConfigArtifactScope artifactScope = mtconfig.MTConfigMill.mTConfigArtifactScopeBuilder()
        .setPackageName(rootNode.getPackage().getQName())
        .setImportsList(new ArrayList<>())
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());

    return artifactScope;
  }

  /**
   * Adds node elements as directly accessible symbols to the artifact scope.
   * @param node astNode for the corresponding scope.
   */
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

  /**
   * Creates CompConfigSymbol with component_platform as name to prevent ambiguity.
   * @param ast AST containing name and platform used for symbol creation.
   * @return symbol with component_platform as name.
   */
  protected  mtconfig._symboltable.CompConfigSymbol create_CompConfig (mtconfig._ast.ASTCompConfig ast)  {
    return mtconfig.MTConfigMill.compConfigSymbolBuilder().setName(ast.getName()+"_"+ast.getPlatform()).build();
  }
}
