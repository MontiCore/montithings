// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import arcbasis._symboltable.PortSymbolDeSer;
import cd4montithings._symboltable.CDPortSymbolDeSer;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Var;
import de.monticore.io.FileFinder;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class MontiThingsGlobalScope extends MontiThingsGlobalScopeTOP {

  @Override
  public MontiThingsGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    this.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol",
      new MethodSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol",
      new FieldSymbolDeSer());
    this.putSymbolDeSer("cd4montithings._symboltable.CDPortSymbol", new VariableSymbolDeSer());
  }

  /*
   * Default implementation tries to load montiarc component models instead of serialized symbol tables.
   * Remove this once it's fixed.
   */
  @Override
  public void loadFileForModelName(@NotNull String modelName) {
    Preconditions.checkNotNull(modelName);
    Optional<ModelCoordinate> mc = FileFinder.findFile(getModelPath(), modelName, ".sym", cache);
    if (mc.isPresent()) {
      addLoadedFile(mc.get().getQualifiedPath().toString());
      IMontiThingsArtifactScope as = getSymbols2Json().load(mc.get().getLocation());
      addSubScope(as);
    }
  }
}
