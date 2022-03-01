// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._ast.ASTArcParameter;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import clockcontrol._ast.ASTCalculationInterval;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCCollectionTypesFullPrettyPrinter;
import genericarc._ast.ASTArcTypeParameter;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.codegen.util.Utils;
import montithings.generator.config.ConfigParams;
import montithings.generator.prettyprinter.CppPrettyPrinter;
import montithings.util.ClassDiagramUtil;
import montithings.util.GenericBindingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static montithings.generator.helper.TypesHelper.java2cppTypeString;

/**
 * Contains methods for printing code to C++ that are easier to express in Java
 * than in Freemarker
 */
public class TypesPrinter {
  /**
   * Converts a SymTypeExpression into an appropriate C++ type
   *
   * @param expression the sym type to convert
   * @return the C++ type
   */
  public static String printCPPTypeName(SymTypeExpression expression) {
    if (expression instanceof SymTypeOfNumericWithSIUnit) {
      expression = ((SymTypeOfNumericWithSIUnit) expression).getNumericType();
    }
    String typeName = expression.print();
    // Workaround for MontiCore Bug that adds component type to variable types
    if (expression.print().endsWith("String")) {
      typeName = "String";
    }
    return java2cppTypeString(typeName);
  }

  /**
   * Converts a SymTypeExpression into an appropriate C++ type, also converts
   * types from class diagrams
   *
   * @param expression the sym type to convert
   * @return the C++ type
   */
  public static String printCPPTypeName(SymTypeExpression expression, ComponentTypeSymbol comp,
    ConfigParams config) {
    if (expression.getTypeInfo() instanceof OOTypeSymbol) {
      return convertMontiCoreTypeNameToCppFQN(expression.getTypeInfo(), comp);
    }
    if (expression instanceof SymTypeOfNumericWithSIUnit) {
      expression = ((SymTypeOfNumericWithSIUnit) expression).getNumericType();
    }
    String typeName = expression.print();
    // Workaround for MontiCore Bug that adds component type to variable types
    if (expression.print().startsWith(comp.getFullName())) {
      typeName = typeName.substring(comp.getFullName().length() + 1);
    }
    return java2cppTypeString(typeName);
  }

  /**
   * Prints the fully qualified C++ name of a port's type
   * @param componentSymbol component the port belongs to
   * @param portSymbol port whose type shall be returned
   * @param config generator config parameters
   * @return the fully qualified C++ name of the portSymbol's type
   */
  public static String printCdPortFQN(ComponentTypeSymbol componentSymbol,
    PortSymbol portSymbol, ConfigParams config) {
    if (!TypesHelper.portUsesCdType(portSymbol)) {
      throw new IllegalArgumentException(
        "Can't print namespace of non-CD type " + portSymbol.getType().getTypeInfo().getFullName());
    }
    TypeSymbol cdTypeSymbol = portSymbol.getType().getTypeInfo();
    return convertMontiCoreTypeNameToCppFQN(cdTypeSymbol, componentSymbol);
  }

  /**
   * Converts MontiCore's Java-like type names, where name parts are separated
   * by dots, to C++ type names, where name parts are separated by double colons
   *
   * @param typeSymbol the type whose FQN shall be returned
   * @param comp component type
   * @return FQN of typeSymbol in C++ notation with double colon separators
   */
  public static String convertMontiCoreTypeNameToCppFQN(TypeSymbol typeSymbol, ComponentTypeSymbol comp) {
    String typeName = typeSymbol.getName();
    //Workaround for component types as the DeSer doesn't create symbols with their fully qualified name
    if (typeName.startsWith(ClassDiagramUtil.COMPONENT_TYPE_PREFIX) && GenericBindingUtil.getComponentFromString(GenericBindingUtil
                    .getEnclosingMontiArcArtifactScope((MontiThingsArtifactScope) comp.getEnclosingScope()),
            typeName.substring(2)) != null) {
      typeName = typeName + "." + typeName;
    }
    return typeName.replaceAll("\\.", "::");
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This
   * takes in to account whether the port is inherited and possible required
   * renamings due to generic type parameters and their actual arguments.
   *
   * @param comp       Symbol of the component which contains the port
   * @param portSymbol Symbol of the port for which the type name should be
   *                   determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentTypeSymbol comp, PortSymbol portSymbol) {
    if (portSymbol.getType() instanceof SymTypeOfNumericWithSIUnit) {
      return ((SymTypeOfNumericWithSIUnit) portSymbol.getType()).getNumericType().getTypeInfo()
        .getName();
    }
    else {
      return portSymbol.getType().getTypeInfo().getName();
    }
  }

  public static String printConstructorArguments(ComponentTypeSymbol comp) {
    StringBuilder result = new StringBuilder();
    MontiThingsFullPrettyPrinter printer = CppPrettyPrinter.getPrinter();
    List<ASTArcParameter> parameters = comp.getAstNode().getHead().getArcParameterList();

    for (int i = 0; i < parameters.size(); i++) {
      ASTArcParameter param = parameters.get(i);
      result.append(java2cppTypeString(printNumericType(param.getSymbol().getType())));

      result.append(" ");
      result.append(param.getName());
      if (param.isPresentDefault()) {
        result.append(" = ");
        if (param.getSymbol().getType() instanceof SymTypeOfNumericWithSIUnit) {
          result.append(Utils.printSIExpression(param.getDefault(), param.getSymbol().getType()));
        }
        else {
          result.append(printer.prettyprint(param.getDefault()));
        }
      }
      if (i < parameters.size() - 1) {
        result.append(", ");
      }
    }

    return result.toString();
  }

  public static String printTypeArguments(List<TypeVarSymbol> types) {
    List<String> typeNames = new ArrayList<>();
    for (TypeVarSymbol type : types) {
      typeNames.add(type.getName());
    }
    return String.join(", ", typeNames);
  }

  public static String printASTTypeArguments(List<ASTArcTypeParameter> types) {
    List<String> typeNames = new ArrayList<>();
    for (ASTArcTypeParameter type : types) {
      typeNames.add(type.getName());
    }
    return String.join(", ", typeNames);
  }

  /**
   * Prints a list of actual type arguments.
   *
   * @param typeArguments The actual type arguments to print
   * @return The printed actual type arguments
   */
  public static String printActualTypeArguments(List<ASTMCTypeArgument> typeArguments) {
    if (typeArguments.size() > 0) {
      return "<" +
        typeArguments.stream().map(TypesPrinter::printTypeArgumentIterate)
          .collect(Collectors.joining(", ")) +
        ">";
    }
    return "";
  }

  /**
   * Prints an actual type argument with sub arguments.
   *
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgumentIterate(ASTMCTypeArgument arg) {
    if (arg instanceof ASTMCBasicGenericType) {
      return printTypeArgument(arg) + printActualTypeArguments(
        ((ASTMCBasicGenericType) arg).getMCTypeArgumentList());
    }
    else {
      return printTypeArgument(arg);
    }
  }

  /**
   * Prints an actual type argument.
   *
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgument(ASTMCTypeArgument arg) {
    if (arg instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) arg).printWithoutTypeArguments();
    }
    return java2cppTypeString(
      arg.printType(new MCCollectionTypesFullPrettyPrinter(new IndentPrinter())));
  }

  public static String printTime(ASTCalculationInterval calculationInterval) {
    if (calculationInterval == null) {
      return "milliseconds(50)";
    }
    return printTime(calculationInterval.getInterval());
  }

  public static String printTime(ASTSIUnitLiteral lit) {
    String time = "milliseconds";
    if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ns")) {
      time = "nanoseconds";
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("μs")) {
      time = "microseconds";
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ms")) {
      time = "milliseconds";
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("s")) {
      time = "seconds";
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("min")) {
      time = "minutes";
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("h")) {
      time = "hours";
    }
    SIUnitLiteralDecoder decoder = new SIUnitLiteralDecoder();
    double value = decoder.getDouble(lit);
    time += "(" + (int) value + ")";
    return time;
  }

  protected static String printNumericType(SymTypeExpression symTypeExpression) {
    if (symTypeExpression instanceof SymTypeOfNumericWithSIUnit)
      return ((SymTypeOfNumericWithSIUnit) symTypeExpression)
        .getNumericType().print();
    else
      return symTypeExpression.print();
  }

  public static String getRealPortCppTypeString(ComponentTypeSymbol comp, PortSymbol port,
    ConfigParams config) {
    if (TypesHelper.portUsesCdType(port)) {
      return printCdPortFQN(comp, port, config);
    }
    else {
      return java2cppTypeString(getRealPortTypeString(comp, port));
    }
  }
}
