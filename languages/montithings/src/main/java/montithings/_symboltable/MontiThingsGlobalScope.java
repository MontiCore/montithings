// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.io.FileFinder;
import de.monticore.io.paths.ModelCoordinate;
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
  }

  /*
   * Default implementation tries to load montiarc component models instead of serialized symbol tables.
   * Remove this once it's fixed.
   */
  @Override
  public void loadFileForModelName(@NotNull String modelName) {
    Preconditions.checkNotNull(modelName);
    Optional<ModelCoordinate> mc = FileFinder.findFile(getModelPath(), modelName, "sym", cache);
    // TODO: currently there are two differing ways in which symbol files are loaded by MontiThings, this one (which is
    // done either way by MontiCore) and the one used in the MontiThingsTool. Using both ways at once leads to errors,
    // as 2 symbol table entries are found for every symbol stemming from a symbol file. Therefore this is currently
    // disabled, but it would probably be better to disable the other way in the long run.
    /*if (mc.isPresent()) {
      addLoadedFile(mc.get().getQualifiedPath().toString());
      IMontiThingsArtifactScope as = getSymbols2Json().load(mc.get().getLocation());
      addSubScope(as);
    }*/
  }
}
