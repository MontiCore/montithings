// (c) https://github.com/MontiCore/monticore
package montithings.util.library;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montithings.MontiThingsMill;

import static montithings.util.SymbolUtil.*;

public class SetType {

  protected static TypeSymbol setSymbol;

  protected static TypeVarSymbol typeVarSymbol;

  public static void addMethodsAndFields(TypeSymbol set, TypeVarSymbol typeVar) {
    setSymbol = set;
    typeVarSymbol = typeVar;
    addFunctionAdd();
    addFunctionAddAll();
    addFunctionContains();
    addFunctionContainsAll();
    addFunctionIsEmpty();
    addFunctionRemove();
    addFunctionRemoveAll();
    addFunctionRetainAll();
    addFunctionSize();
  }

  /* ============================================================ */
  /* ========================== METHODS ========================= */
  /* ============================================================ */

  protected static void addFunctionAdd() {
    FunctionSymbol function = createMethod("add");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getSetOfXSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionAddAll() {
    FunctionSymbol function = createMethod("addAll");
    addParam(function, "c", getSetOfXSymType());
    function.setReturnType(getSetOfXSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionContains() {
    FunctionSymbol function = createMethod("contains");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getBoolSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionContainsAll() {
    FunctionSymbol function = createMethod("containsAll");
    addParam(function, "c", getSetOfXSymType());
    function.setReturnType(getBoolSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionCount() {
    FunctionSymbol function = createMethod("count");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getIntSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionIsEmpty() {
    FunctionSymbol function = createMethod("isEmpty");
    function.setReturnType(getBoolSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemove() {
    FunctionSymbol function = createMethod("remove");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getSetOfXSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemoveAll() {
    FunctionSymbol function = createMethod("removeAll");
    addParam(function, "c", getSetOfXSymType());
    function.setReturnType(getSetOfXSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRetainAll() {
    FunctionSymbol function = createMethod("retainAll");
    addParam(function, "c", getSetOfXSymType());
    function.setReturnType(getSetOfXSymType());
    setSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionSize() {
    FunctionSymbol function = createMethod("size");
    function.setReturnType(getIntSymType());
    setSymbol.getSpannedScope().add(function);
  }

  /* ============================================================ */
  /* ========================= HELPERS ========================== */
  /* ============================================================ */

  protected static FunctionSymbol createMethod(String name) {
    return MontiThingsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(setSymbol.getSpannedScope())
        .setSpannedScope(MontiThingsMill.scope())
        .build();
  }

  protected static SymTypeExpression getSetOfXSymType() {
    return SymTypeExpressionFactory
        .createGenerics(setSymbol, SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
  }
}
