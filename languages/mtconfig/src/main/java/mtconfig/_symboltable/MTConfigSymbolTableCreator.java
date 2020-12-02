// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import com.google.common.base.Preconditions;
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
    MTConfigArtifactScope artifactScope = mtconfig.MTConfigMill.mTConfigArtifactScopeBuilder()
        .setPackageName(rootNode.getPackage().getQName())
        .setImportList(new ArrayList<>())
        .build();
    putOnStack(artifactScope);
    rootNode.accept(getRealThis());
    return artifactScope;
  }
}
