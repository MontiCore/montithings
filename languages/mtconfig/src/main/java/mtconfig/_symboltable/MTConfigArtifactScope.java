// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.utils.Names;

import java.util.List;
import java.util.Optional;

public class MTConfigArtifactScope extends MTConfigArtifactScopeTOP{

  public MTConfigArtifactScope() {
  }

  public MTConfigArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public MTConfigArtifactScope(Optional<IMTConfigScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @Override
  public  String getName ()  {
    if (!isPresentName()&&!getCompConfigSymbols().isEmpty()) {
      String name = Names.getSimpleName(getCompConfigSymbols().values().get(0).getFullName());
        setName(name);
    }
    return super.getName();
  }

  @Override
  public  boolean isPresentName ()  {
    if (!this.name.isPresent()&&!getCompConfigSymbols().isEmpty()) {
      String name = Names.getSimpleName(getCompConfigSymbols().values().get(0).getFullName());
      setName(name);
    }
    return this.name.isPresent();
  }
}
