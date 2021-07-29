// (c) https://github.com/MontiCore/monticore
package montithings.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import montithings.MontiThingsMill;
import montithings._auxiliary.BasicSymbolsMillForMontiThings;

/**
 * Makes MontiCores builders easier to use
 */
public class SymbolUtil {

  public static FunctionSymbol createFunction(String name, IBasicSymbolsScope enclosingScope) {
    return createFunction(name, SymTypeExpressionFactory.createTypeConstant("void"),
      enclosingScope);
  }

  public static FunctionSymbol createFunction(String name, SymTypeExpression returnType,
    IBasicSymbolsScope enclosingScope) {
    FunctionSymbol func = MontiThingsMill.functionSymbolBuilder()
      .setName(name)
      .setReturnType(returnType)
      .setEnclosingScope(enclosingScope)
      .build();
    func.setSpannedScope(MontiThingsMill.scope());
    enclosingScope.add(func);
    returnType.getTypeInfo().addFunctionSymbol(func);
    return func;
  }

  public static void addParam(FunctionSymbol function, String paramName,
    SymTypeExpression paramType) {
    VariableSymbol param = BasicSymbolsMillForMontiThings.variableSymbolBuilder()
      .setName(paramName)
      .setEnclosingScope(function.getSpannedScope())
      .setType(paramType)
      .build();

    //add parameter to method
    function.getSpannedScope().add(param);
  }

}
