// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import mtconfig.MTConfigMill;
import mtconfig._ast.ASTCompConfig;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._ast.ASTPortTemplateTag;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Builds the symbol table.
 */
public class MTConfigScopesGenitor extends MTConfigScopesGenitorTOP {

  /**
   * Creates MTConfigArtifactScope from ast.
   * The package is set in the ArtifactScope.
   *
   * @param rootNode AST root used for creation.
   * @return scope created from given AST.
   */
  @Override
  public IMTConfigArtifactScope createFromAST(@NotNull ASTMTConfigUnit rootNode) {
    Preconditions.checkArgument(rootNode != null);

    IMTConfigArtifactScope artifactScope = MTConfigMill.artifactScope();
    artifactScope.setPackageName(rootNode.getPackage().getQName());
    artifactScope.setImportsList(new ArrayList<>());
    putOnStack(artifactScope);
    rootNode.accept(getTraverser());
    return artifactScope;
  }

  /**
   * Adds node elements as directly accessible symbols to the artifact scope.
   *
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
    mtconfig._symboltable.IMTConfigScope scope = createScope(false);
    // putOnStack(scope);

    // scope -> ast
    scope.setAstNode(node);

    // ast -> scope
    node.setSpannedScope(scope);
    initScopeHP1(scope);
  }

  @Override
  public void endVisit(ASTMTConfigUnit node) {
    // intentionally left empty
  }

  /**
   * Creates CompConfigSymbol with component_platform as name to prevent ambiguity.
   *
   * @param ast AST containing name and platform used for symbol creation.
   * @return symbol with component_platform as name.
   */
  protected CompConfigSymbol create_CompConfig(ASTCompConfig ast) {
    return MTConfigMill.compConfigSymbolBuilder().setName(ast.getName() + "_" + ast.getPlatform())
      .build();
  }

  @Override public void visit(ASTCompConfig node) {
    super.visit(node);
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope()
      .resolveComponentType(node.getComponentType());
    comp.ifPresent(node::setComponentTypeSymbol);
  }

  @Override public void visit(ASTPortTemplateTag node) {
    super.visit(node);
    String packageName = node.getEnclosingScope().getSpanningSymbol().getPackageName();
    String compName = ((ASTCompConfig) node.getEnclosingScope().getSpanningSymbol().getAstNode())
      .getName();
    String portFQN = packageName + "." + compName + "." + node.getPort();

    Optional<PortSymbol> port = node.getEnclosingScope().resolvePort(portFQN);
    port.ifPresent(node::setPortSymbol);
  }
}
