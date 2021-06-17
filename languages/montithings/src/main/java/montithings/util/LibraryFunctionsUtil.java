// (c) https://github.com/MontiCore/monticore
package montithings.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeVariable;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsScope;
import org.codehaus.commons.nullanalysis.NotNull;

import static montithings.util.SymbolUtil.addParam;
import static montithings.util.SymbolUtil.createFunction;


public class LibraryFunctionsUtil {

  /* ============================================================ */
  /* ==================== AGGREGATED METHODS ==================== */
  /* ============================================================ */

  public static void addAllLibraryFunctions(@NotNull IMontiThingsScope scope) {
    addMTLibraryFunctions(scope);
    addMTReplayLibraryFunctions(scope);
  }

  public static void addMTLibraryFunctions(@NotNull IMontiThingsScope scope) {
    addFunctionLog(scope);
    addFunctionDelay(scope);
    addFunctionNow(scope);
    addFunctionNow_Ns(scope);
  }

  public static void addMTReplayLibraryFunctions(@NotNull IMontiThingsScope scope) {
    addFunctionGetNanoTimestamp(scope);
    addFunctionDelayNanoseconds(scope);
    addFunctionSubtract(scope);
    addFunctionGetNsFromMap(scope);
    addFunctionStoreNsInMap(scope);
    addFunctionNd(scope);
  }

  /* ============================================================ */
  /* ========================= LIBRARY ========================== */
  /* ============================================================ */




  public static void addFunctionLog(@NotNull IMontiThingsScope scope) {
    FunctionSymbol log = createFunction("log", scope);
    addParam(log, "message", SymTypeExpressionFactory.createTypeObject("String", scope));
  }

  public static void addFunctionDelay(@NotNull IMontiThingsScope scope) {
    FunctionSymbol delay = createFunction("delay", scope);
    addParam(delay, "milliseconds", SymTypeExpressionFactory.createTypeConstant("int"));
  }

  public static void addFunctionNow(@NotNull IMontiThingsScope scope) {
    createFunction("now", SymTypeExpressionFactory.createTypeConstant("long"), scope);
  }

  public static void addFunctionNow_Ns(@NotNull IMontiThingsScope scope) {
    createFunction("now_ns", SymTypeExpressionFactory.createTypeObject("String", scope), scope);
  }

  /* ============================================================ */
  /* ===================== MTReplayLibrary ====================== */
  /* ============================================================ */
  // Library functions added by dds/replayer/MTReplayLibrary
  // assume long = unsigned long long since Monticore does not support all types

  public static void addFunctionGetNanoTimestamp(@NotNull IMontiThingsScope scope) {
    createFunction("getNanoTimestamp", SymTypeExpressionFactory.createTypeConstant("long"),
      scope);
  }

  public static void addFunctionDelayNanoseconds(@NotNull IMontiThingsScope scope) {
    FunctionSymbol delayNanoseconds = createFunction("delayNanoseconds", scope);
    addParam(delayNanoseconds, "nanoseconds", SymTypeExpressionFactory.createTypeConstant("long"));
  }

  public static void addFunctionSubtract(@NotNull IMontiThingsScope scope) {
    FunctionSymbol subtract = createFunction("subtract", SymTypeExpressionFactory.createTypeConstant("long"), scope);
    addParam(subtract, "v1", SymTypeExpressionFactory.createTypeConstant("long"));
    addParam(subtract, "v2", SymTypeExpressionFactory.createTypeConstant("long"));
  }

  public static void addFunctionGetNsFromMap(@NotNull IMontiThingsScope scope) {
    FunctionSymbol getNsFromMap = createFunction("getNsFromMap", SymTypeExpressionFactory.createTypeConstant("long"), scope);
    addParam(getNsFromMap, "index", SymTypeExpressionFactory.createTypeConstant("int"));
  }

  public static void addFunctionStoreNsInMap(@NotNull IMontiThingsScope scope) {
    FunctionSymbol storeNsInMap = createFunction("storeNsInMap", scope);
    addParam(storeNsInMap, "index", SymTypeExpressionFactory.createTypeConstant("int"));
    addParam(storeNsInMap, "ts", SymTypeExpressionFactory.createTypeConstant("long"));
  }

  // Non-determinism wrapper for replayer
  public static void addFunctionNd(@NotNull IMontiThingsScope scope) {
    TypeVarSymbol returnNd = MontiThingsMill
      .typeVarSymbolBuilder()
      .setName("T")
      .build();

    returnNd.setSpannedScope(MontiThingsMill.scope());
    returnNd.setEnclosingScope(scope);
    SymTypeVariable returnType = SymTypeExpressionFactory.createTypeVariable(returnNd);

    FunctionSymbol nd = createFunction("nd", returnType, scope);
    addParam(nd, "value", SymTypeExpressionFactory.createTypeVariable(returnNd));
  }
}
