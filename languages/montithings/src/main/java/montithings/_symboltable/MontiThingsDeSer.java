// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.symboltable.serialization.ISymbolDeSer;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * MontiThingsDeSerTOP but also allows to suppress useless warnings about
 * not being able to deserialize symbols you never intended to
 * deserialize
 */
public class MontiThingsDeSer extends MontiThingsDeSerTOP {
  protected List<String> symbolKindsToIgnore = new ArrayList<>();

  public void ignoreSymbolKind(String fqn) {
    symbolKindsToIgnore.add(fqn);
  }

  /**
   * This is basically a copy paste from the generated code, but with an additional if statement
   * for ignored symbol kinds
   *
   * @param scope     IMontiThingsScope to put symbols in
   * @param scopeJson json to deserialize
   */
  @Override protected void deserializeSymbols(IMontiThingsScope scope, JsonObject scopeJson) {

    for (JsonObject symbol : JsonDeSers.getSymbols(scopeJson)) {
      String kind = JsonDeSers.getKind(symbol);

      // Ignore unwanted symbol kinds
      if (symbolKindsToIgnore.contains(kind)) {
        continue;
      }

      // The rest of this method is a copy-paste from the generated code because
      // its not in a single method that we could just override
      ISymbolDeSer deSer = montithings.MontiThingsMill
        .globalScope().getSymbolDeSer(kind);
      if (null == deSer) {
        Log.warn("0xA1234xx11645 No DeSer found to deserialize symbol of kind `" + kind
          + "`. The following will be ignored: " + symbol);
        continue;
      }

      if ("arcbasis._symboltable.ComponentInstanceSymbol".equals(kind)
        || "arcbasis._symboltable.ComponentInstanceSymbol".equals(deSer.getSerializedKind())) {
        arcbasis._symboltable.ComponentInstanceSymbol s0 = (arcbasis._symboltable.ComponentInstanceSymbol) deSer
          .deserialize(symbol);
        scope.add(s0);
      }
      else if ("arcbasis._symboltable.ComponentTypeSymbol".equals(kind)
        || "arcbasis._symboltable.ComponentTypeSymbol".equals(deSer.getSerializedKind())) {
        arcbasis._symboltable.ComponentTypeSymbol s1 = (arcbasis._symboltable.ComponentTypeSymbol) deSer
          .deserialize(symbol);
        scope.add(s1);
        scope.addSubScope(s1.getSpannedScope());
      }
      else if ("arcbasis._symboltable.PortSymbol".equals(kind)
        || "arcbasis._symboltable.PortSymbol".equals(deSer.getSerializedKind())) {
        arcbasis._symboltable.PortSymbol s2 = (arcbasis._symboltable.PortSymbol) deSer
          .deserialize(symbol);
        scope.add(s2);
      }
      else if ("behavior._symboltable.EveryBlockSymbol".equals(kind)
        || "behavior._symboltable.EveryBlockSymbol".equals(deSer.getSerializedKind())) {
        behavior._symboltable.EveryBlockSymbol s3 = (behavior._symboltable.EveryBlockSymbol) deSer
          .deserialize(symbol);
        scope.add(s3);
      }
      else if ("de.monticore.scbasis._symboltable.SCStateSymbol".equals(kind)
        || "de.monticore.scbasis._symboltable.SCStateSymbol".equals(deSer.getSerializedKind())) {
        de.monticore.scbasis._symboltable.SCStateSymbol s4 = (de.monticore.scbasis._symboltable.SCStateSymbol) deSer
          .deserialize(symbol);
        scope.add(s4);
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol s5 = (de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol) deSer
          .deserialize(symbol);
        scope.add(s5);
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol s6 = (de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol) deSer
          .deserialize(symbol);
        scope.add(s6);
        scope.addSubScope(s6.getSpannedScope());
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.TypeSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.TypeSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.TypeSymbol s7 = (de.monticore.symbols.basicsymbols._symboltable.TypeSymbol) deSer
          .deserialize(symbol);
        scope.add(s7);
        scope.addSubScope(s7.getSpannedScope());
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol s8 = (de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol) deSer
          .deserialize(symbol);
        scope.add(s8);
      }
      else if ("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol".equals(kind)
        || "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.basicsymbols._symboltable.VariableSymbol s9 = (de.monticore.symbols.basicsymbols._symboltable.VariableSymbol) deSer
          .deserialize(symbol);
        scope.add(s9);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.FieldSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.FieldSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.FieldSymbol s10 = (de.monticore.symbols.oosymbols._symboltable.FieldSymbol) deSer
          .deserialize(symbol);
        scope.add(s10);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.MethodSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.MethodSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.MethodSymbol s11 = (de.monticore.symbols.oosymbols._symboltable.MethodSymbol) deSer
          .deserialize(symbol);
        scope.add(s11);
      }
      else if ("de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol".equals(kind)
        || "de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol"
        .equals(deSer.getSerializedKind())) {
        de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol s12 = (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol) deSer
          .deserialize(symbol);
        scope.add(s12);
        scope.addSubScope(s12.getSpannedScope());
      }
      else if ("genericarc._symboltable.ArcTypeParameterSymbol".equals(kind)
        || "genericarc._symboltable.ArcTypeParameterSymbol".equals(deSer.getSerializedKind())) {
        genericarc._symboltable.ArcTypeParameterSymbol s13 = (genericarc._symboltable.ArcTypeParameterSymbol) deSer
          .deserialize(symbol);
        scope.add(s13);
      }
      else if ("variablearc._symboltable.ArcFeatureSymbol".equals(kind)
        || "variablearc._symboltable.ArcFeatureSymbol".equals(deSer.getSerializedKind())) {
        variablearc._symboltable.ArcFeatureSymbol s14 = (variablearc._symboltable.ArcFeatureSymbol) deSer
          .deserialize(symbol);
        scope.add(s14);
      }
      else {
        Log.warn("0xA1634xx11645 Unable to integrate deserialization with DeSer for kind `" + kind
          + "`. The following will be ignored: " + symbol);
      }
    }
  }
}
