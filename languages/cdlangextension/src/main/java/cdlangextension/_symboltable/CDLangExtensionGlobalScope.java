// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;


public class CDLangExtensionGlobalScope extends CDLangExtensionGlobalScopeTOP {
  @Override public CDLangExtensionGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    this.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
  }


}
