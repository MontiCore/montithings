// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

import cdlangextension.CDLangExtensionMill;
import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class CDLangExtensionDeSer extends CDLangExtensionDeSerTOP {

  protected List<String> symbolKindsToIgnore = new ArrayList<>();

  public void ignoreSymbolKind(String fqn) {
    symbolKindsToIgnore.add(fqn);
  }


  /**
   * This is basically a copy paste from the generated code, but with an additional if statement
   * for ignored symbol kinds
   * @param scope ICDLangExtensionScope to put symbols in
   * @param scopeJson json to deserialize
   */
  protected void deserializeSymbols (ICDLangExtensionScope scope, JsonObject scopeJson)  {

    for (JsonObject symbol : JsonDeSers.getSymbols(scopeJson)) {
      String kind = JsonDeSers.getKind(symbol);

      // Ignore unwanted symbol kinds
      if (symbolKindsToIgnore.contains(kind)) {
        continue;
      }

      // The rest of this method is a copy-paste from the generated code because
      // its not in a single method that we could just override
      ISymbolDeSer deSer = CDLangExtensionMill.globalScope().getSymbolDeSer(kind);
      if (null == deSer) {
        Log.warn("0xA1234xx30359 No DeSer found to deserialize symbol of kind `" + kind
          + "`. The following will be ignored: " + symbol);
        continue;
      }

      if ("cdlangextension._symboltable.CDEImportStatementSymbol".equals(kind)
        || "cdlangextension._symboltable.CDEImportStatementSymbol".equals(deSer.getSerializedKind())) {
        cdlangextension._symboltable.CDEImportStatementSymbol s0 = (cdlangextension._symboltable.CDEImportStatementSymbol) deSer.deserialize(symbol);
        scope.add(s0);
      }
      else if ("cdlangextension._symboltable.CDLangExtensionUnitSymbol".equals(kind)
        || "cdlangextension._symboltable.CDLangExtensionUnitSymbol".equals(deSer.getSerializedKind())) {
        cdlangextension._symboltable.CDLangExtensionUnitSymbol s1 = (cdlangextension._symboltable.CDLangExtensionUnitSymbol) deSer.deserialize(symbol);
        scope.add(s1);
        scope.addSubScope(s1.getSpannedScope());
      }
      else if ("cdlangextension._symboltable.DepLanguageSymbol".equals(kind)
        || "cdlangextension._symboltable.DepLanguageSymbol".equals(deSer.getSerializedKind())) {
        cdlangextension._symboltable.DepLanguageSymbol s2 = (cdlangextension._symboltable.DepLanguageSymbol) deSer.deserialize(symbol);
        scope.add(s2);
        scope.addSubScope(s2.getSpannedScope());
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol s3 = (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol) deSer.deserialize(symbol);
        scope.add(s3);
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol s4 = (de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol) deSer.deserialize(symbol);
        scope.add(s4);
        scope.addSubScope(s4.getSpannedScope());
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.TypeSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.TypeSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.TypeSymbol s5 = (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol) deSer.deserialize(symbol);
        scope.add(s5);
        scope.addSubScope(s5.getSpannedScope());
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol s6 = (de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol) deSer.deserialize(symbol);
        scope.add(s6);
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.VariableSymbol s7 = (de.monticore.symbols.basicsymbols._symboltable.VariableSymbol) deSer.deserialize(symbol);
        scope.add(s7);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.FieldSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.FieldSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.FieldSymbol s8 = (de.monticore.symbols.oosymbols._symboltable.FieldSymbol) deSer.deserialize(symbol);
        scope.add(s8);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.MethodSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.MethodSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.MethodSymbol s9 = (de.monticore.symbols.oosymbols._symboltable.MethodSymbol) deSer.deserialize(symbol);
        scope.add(s9);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol s10 = (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol) deSer.deserialize(symbol);
        scope.add(s10);
        scope.addSubScope(s10.getSpannedScope());
      }
      else {
        Log.warn("0xA1634xx30359 Unable to integrate deserialization with DeSer for kind `" + kind
          + "`. The following will be ignored: " + symbol);
      }
    }

  }
}
