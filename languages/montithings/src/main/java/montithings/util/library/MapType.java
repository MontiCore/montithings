package montithings.util.library;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montithings.MontiThingsMill;

import static montithings.util.SymbolUtil.*;

public class MapType {
  protected static TypeSymbol mapSymbol;

  protected static TypeVarSymbol typeVarSymbolKey;

  protected static TypeVarSymbol typeVarSymbolValue;

  public static void addMethodsAndFields(TypeSymbol map, TypeVarSymbol typeVarKey, TypeVarSymbol typeVarValue) {
    mapSymbol = map;
    typeVarSymbolKey = typeVarKey;
    typeVarSymbolValue = typeVarValue;
    addFunctionClear();
    addFunctionContainsKey();
    addFunctionContainsValue();
    addFunctionEquals();
    addFunctionGet();
    addFunctionPut();
    addFunctionPutAll();
    addFunctionRemove();
    addFunctionRemove2();
    addFunctionSize();
  }

  /* ============================================================ */
  /* ========================== METHODS ========================= */
  /* ============================================================ */

  protected static void addFunctionClear() {
    FunctionSymbol function = createMethod("clear");
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionContainsKey() {
    FunctionSymbol function = createMethod("contains");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey));
    function.setReturnType(getBoolSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionContainsValue() {
    FunctionSymbol function = createMethod("contains");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    function.setReturnType(getBoolSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionEquals() {
    FunctionSymbol function = createMethod("equals");
    addParam(function, "o", getMapOfXSymType());
    function.setReturnType(getBoolSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionGet() {
    FunctionSymbol function = createMethod("get");
    addParam(function, "key", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey));
    function.setReturnType(SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionPut() {
    FunctionSymbol function = createMethod("put");
    addParam(function, "K", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey));
    addParam(function, "V", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    function.setReturnType(SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionPutAll() {
    FunctionSymbol function = createMethod("putAll");
    addParam(function, "m", getMapOfXSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemove() {
    FunctionSymbol function = createMethod("remove");
    addParam(function, "key", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey));
    function.setReturnType(SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemove2() {
    FunctionSymbol function = createMethod("removeAll");
    addParam(function, "key", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey));
    addParam(function, "value", SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
    function.setReturnType(getBoolSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionSize() {
    FunctionSymbol function = createMethod("size");
    function.setReturnType(getIntSymType());
    mapSymbol.getSpannedScope().add(function);
  }

  /* ============================================================ */
  /* ========================= HELPERS ========================== */
  /* ============================================================ */

  protected static FunctionSymbol createMethod(String name) {
    return MontiThingsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(mapSymbol.getSpannedScope())
        .setSpannedScope(MontiThingsMill.scope())
        .build();
  }

  protected static SymTypeExpression getMapOfXSymType() {
    return SymTypeExpressionFactory
        .createGenerics(mapSymbol, SymTypeExpressionFactory.createTypeVariable(typeVarSymbolKey), SymTypeExpressionFactory.createTypeVariable(typeVarSymbolValue));
  }
}
