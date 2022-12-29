/* (c) https://github.com/MontiCore/monticore */
package montithings.util.library;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montithings.MontiThingsMill;

import static montithings.util.SymbolUtil.*;

public class ListType {
  protected static TypeSymbol listSymbol;
  protected static TypeVarSymbol typeVarSymbol;

  public static void addMethodsAndFields(TypeSymbol list, TypeVarSymbol typeVar) {
    listSymbol = list;
    typeVarSymbol = typeVar;
    addFunctionAdd();
    addFunctionAdd2();
    addFunctionAddAll();
    addFunctionAddAll2();
    addFunctionClear();
    addFunctionContains();
    addFunctionContainsAll();
    addFunctionEquals();
    addFunctionGet();
    addFunctionIndexOf();
    addFunctionLastIndexOf();
    addFunctionIsEmpty();
    addFunctionRemove();
    addFunctionRemoveAtIndex();
    addFunctionRemoveAll();
    addFunctionRetainAll();
    addFunctionSet();
    addFunctionSize();
    addFunctionSubList();
  }

  protected static void addFunctionAdd() {
    FunctionSymbol function = createMethod("add");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionAdd2() {
    FunctionSymbol function = createMethod("add");
    addParam(function, "index", getIntSymType());
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionAddAll() {
    FunctionSymbol function = createMethod("addAll");
    addParam(function, "c", getListOfXSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionAddAll2() {
    FunctionSymbol function = createMethod("addAll");
    addParam(function, "index", getIntSymType());
    addParam(function, "c", getListOfXSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionClear() {
    FunctionSymbol function = createMethod("clear");
    listSymbol.getSpannedScope().add(function);
  }


  protected static void addFunctionContains() {
    FunctionSymbol function = createMethod("contains");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getBoolSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionContainsAll() {
    FunctionSymbol function = createMethod("containsAll");
    addParam(function, "c", getListOfXSymType());
    function.setReturnType(getBoolSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionEquals() {
    FunctionSymbol function = createMethod("equals");
    addParam(function, "o", getListOfXSymType());
    function.setReturnType(getBoolSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionGet() {
    FunctionSymbol function = createMethod("get");
    addParam(function, "index", getIntSymType());
    function.setReturnType(SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionIndexOf() {
    FunctionSymbol function = createMethod("indexOf");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getIntSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionLastIndexOf() {
    FunctionSymbol function = createMethod("lastIndexOf");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getIntSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionIsEmpty() {
    FunctionSymbol function = createMethod("isEmpty");
    function.setReturnType(getBoolSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemove() {
    FunctionSymbol function = createMethod("remove");
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemoveAtIndex() {
    FunctionSymbol function = createMethod("removeAtIndex");
    addParam(function, "index", getIntSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRemoveAll() {
    FunctionSymbol function = createMethod("removeAll");
    addParam(function, "c", getListOfXSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionRetainAll() {
    FunctionSymbol function = createMethod("retainAll");
    addParam(function, "c", getListOfXSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionSet() {
    FunctionSymbol function = createMethod("set");
    addParam(function, "index", getIntSymType());
    addParam(function, "o", SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionSize() {
    FunctionSymbol function = createMethod("size");
    function.setReturnType(getIntSymType());
    listSymbol.getSpannedScope().add(function);
  }

  protected static void addFunctionSubList() {
    FunctionSymbol function = createMethod("subList");
    addParam(function, "fromIndex", getIntSymType());
    addParam(function, "toIndex", getIntSymType());
    function.setReturnType(getListOfXSymType());
    listSymbol.getSpannedScope().add(function);
  }

  /* ============================================================ */
  /* ========================= HELPERS ========================== */
  /* ============================================================ */

  protected static FunctionSymbol createMethod(String name) {
    return MontiThingsMill.functionSymbolBuilder().setName(name).setEnclosingScope(listSymbol.getSpannedScope()).setSpannedScope(MontiThingsMill.scope()).build();
  }

  protected static SymTypeExpression getListOfXSymType() {
    return SymTypeExpressionFactory.createGenerics(listSymbol, new SymTypeExpression[]{SymTypeExpressionFactory.createTypeVariable(typeVarSymbol)});
  }
}
