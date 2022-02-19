// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.util;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import cdlangextension._ast.ASTCDEImportStatement;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.TypesHelper;
import montithings.generator.helper.TypesPrinter;
import montithings.generator.prettyprinter.CppPrettyPrinter;
import montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import montithings.util.ClassDiagramUtil;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

import static montithings.generator.helper.TypesHelper.getConversionFactorFromSourceAndTarget;
import static montithings.generator.helper.TypesHelper.isSIUnitPort;

@SuppressWarnings("unused")
public class Utils {

  private static List<String> packages;

  public static String getDeployFile(ComponentTypeSymbol comp) {
    String filename = comp.getFullName();
    int indexComponentName = filename.length() - comp.getName().length();
    filename = filename.substring(0, indexComponentName);
    filename = filename.replace(".", "/");
    filename += "Deploy" + comp.getName() + ".cpp";
    return filename;
  }

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  public static String printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    int i = 1;
    for (VariableSymbol param : comp.getParameters()) {
      SymTypeExpression type = param.getType();
      if (type instanceof SymTypeOfNumericWithSIUnit){
        type = ((SymTypeOfNumericWithSIUnit) type).getNumericType();
      }
      s.append(TypesHelper.java2cppTypeString(type.print()));
      s.append(" ");
      s.append(param.getName());
      if (i != comp.getParameters().size()) {
        s.append(',');
      }
      i++;
    }
    return s.toString();
  }

  public static String printSerializeParameters(ComponentInstanceSymbol compInstance) {
    List<VariableSymbol> params = compInstance.getType().getParameters();
    List<String> paramValues = ComponentHelper.getParamValues(compInstance);
    Preconditions.checkArgument(params.size() == paramValues.size(),
      "0xMT2000 Number of parameters does not match number of arguments for instance ",
      compInstance.getName());

    StringBuilder result = new StringBuilder();
    for (int i = 0 ; i < params.size() ; i++) {
      result.append("config[\"").append(params.get(i).getName()).append("\"]")
        .append(" = dataToJson (").append(paramValues.get(i)).append(");\n");
    }
    return result.toString();
  }

  public static String printSIParameters(ComponentTypeSymbol comp, ComponentInstanceSymbol compInstance){
    StringBuilder result = new StringBuilder();
    for (PortSymbol ps : compInstance.getType().getAllPorts()){
      if (isSIUnitPort(ps) && ps.isIncoming()){
        for (ASTConnector c : comp.getAstNode().getConnectors()){
          for (ASTPortAccess portAccess : c.getTargetList()){
            Optional<PortSymbol> ops = ComponentHelper.getPortSymbolFromPortAccess(portAccess);
            if(ops.isPresent() && ops.get().equals(ps)){
              result.append("config[\"").append(ps.getName()).append("ConversionFactor\"]")
                .append(" = dataToJson (")
                .append(getConversionFactorFromSourceAndTarget(c.getSource(), portAccess))
                .append(");\n");
            }
          }
        }
      }
    }
    return result.toString();
  }

  /**
   * Prints the component's imports
   */
  public static String printImports(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    for (ImportStatement _import : ComponentHelper.getImports(comp)) {
      s.append("import ").append(_import.getStatement());
      if (_import.isStar()) {
        s.append(".*");
      }
    }
    for (ComponentTypeSymbol inner : comp.getInnerComponents()) {
      s.append("import ");
      s.append(printPackageWithoutKeyWordAndSemicolon(inner));
      s.append(".");
      s.append(inner.getName());
    }
    return s.toString();
  }

  /**
   * Prints a member of given visibility name and type
   */
  public static String printMember(String type, String name, String initialValue) {
    String result = type + " " + name;
    if (!initialValue.equals("")) {
      result += "=" + initialValue;
    }
    return result + ";";
  }

  public static String printMember(String type, String name) {
    return printMember(type, name, "");
  }

  /**
   * Prints members for configuration parameters.
   */
  public static String printConfigParameters(ComponentTypeSymbol comp, ConfigParams config) {
    StringBuilder s = new StringBuilder();
    for (VariableSymbol param : comp.getParameters()) {
      if (param.getAstNode() instanceof ASTArcParameter) {
        ASTArcParameter parameter = (ASTArcParameter) param.getAstNode();
        if(param.getType() instanceof SymTypeOfNumericWithSIUnit){
          s.append(printMember(TypesPrinter.printCPPTypeName(param.getType(), comp, config),
                  param.getName(), printSIExpression(parameter.getDefault(), param.getType())));
        }
        else {
          s.append(printMember(TypesPrinter.printCPPTypeName(param.getType(), comp, config),
                  param.getName(), printExpression(parameter.getDefault())));
        }
      }
      else {
        s.append(printMember(TypesPrinter.printCPPTypeName(param.getType(), comp, config),
          param.getName()));
      }
    }
    return s.toString();
  }

  /**
   * Prints members for variables
   */
  public static String printVariables(ComponentTypeSymbol comp, ConfigParams config) {
    return printVariables(comp, config, true);
  }

  public static String printVariables(ComponentTypeSymbol comp, ConfigParams config, boolean printStateVariablePrefix) {
    StringBuilder s = new StringBuilder();

    // Sort to print params before fields
    List<VariableSymbol> vars = ComponentHelper.getVariablesAndParameters(comp);

    for (VariableSymbol variable : vars) {
      String initialValue = getInitialValue(variable);
      if (!printStateVariablePrefix) {
        initialValue = initialValue.replaceAll(Identifier.getStateName() + ".", "");
      }
      String typeName = TypesPrinter.printCPPTypeName(variable.getType(), comp, config);
      s.append(printMember(typeName, variable.getName(), initialValue));
    }
    return s.toString();
  }

  public static String getInitialValue (VariableSymbol variable) {
    String initialValue = "";
    if (variable.getAstNode() instanceof ASTArcField) {
      ASTArcField field = (ASTArcField) variable.getAstNode();
      if(variable.getType() instanceof SymTypeOfNumericWithSIUnit){
        initialValue = printSIExpression(field.getInitial(), variable.getType());
      }
      else {
        initialValue = printExpression(field.getInitial());
      }
    }
    return initialValue;
  }

  /**
   * Prints formal parameters of a component.
   */
  public static String printFormalTypeParameters(ComponentTypeSymbol comp) {
    return printFormalTypeParameters(comp, false);
  }

  public static String printFormalTypeParameters(ComponentTypeSymbol comp,
    Boolean withClassPrefix) {
    StringBuilder s = new StringBuilder();
    int i = 1;
    if (hasTypeParameter(comp)) {
      s.append('<');
      for (String generic : getGenericParameters(comp)) {
        if (withClassPrefix) {
          s.append("class ");
        }
        s.append(generic);
        if (i != ((getGenericParameters(comp)).size())) {
          s.append(',');
        }
        i++;
      }
      s.append('>');
    }
    return s.toString();
  }

  public static String printTemplateArguments(ComponentTypeSymbol comp) {
    if (hasTypeParameter(comp)) {
      return "template" + printFormalTypeParameters(comp, true);
    }
    return "";
  }

  public static List<String> getGenericParameters(ComponentTypeSymbol comp) {
    List<String> output = new ArrayList<>();
    /*TODO Check why not all typeParameters exist in ComponentTypeSymbols
      if (comp.hasTypeParameter()) {
      List<TypeVarSymbol> parameterList = comp.getTypeParameters();
      for (TypeVarSymbol typeParameter : parameterList) {
        output.add(typeParameter.getName());
      }
    }*/
    if (comp.getAstNode().getHead() instanceof ASTGenericComponentHead &&
      !((ASTGenericComponentHead) comp.getAstNode().getHead()).isEmptyArcTypeParameters()) {
      List<ASTArcTypeParameter> parameterList = ((ASTGenericComponentHead) comp.getAstNode()
        .getHead()).getArcTypeParameterList();
      for (ASTArcTypeParameter typeParameter : parameterList) {
        output.add(typeParameter.getName());
      }
    }
    return output;
  }

  /*TODO Check why not all typeParameters exist in ComponentTypeSymbols,
   * then this redundant method can be replaced with comp.hasTypeParameter().
   */
  public static boolean hasTypeParameter(ComponentTypeSymbol comp) {
    return comp.getAstNode().getHead() instanceof ASTGenericComponentHead &&
      !((ASTGenericComponentHead) comp.getAstNode().getHead()).isEmptyArcTypeParameters();
  }

  /*TODO Check why not all typeParameters exist in ComponentTypeSymbols,
   * then this redundant method can be replaced with comp.getTypeParameter().
   */
  public static List<ASTArcTypeParameter> getTypeParameters(ComponentTypeSymbol comp) {
    if (comp.getAstNode().getHead() instanceof ASTGenericComponentHead) {
      return ((ASTGenericComponentHead) comp.getAstNode().getHead()).getArcTypeParameterList();
    }
    return new LinkedList<>();
  }

  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  public static String printPackage(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    if (comp.isInnerComponent() && comp.getOuterComponent().isPresent()) {
      s.append("package ");
      s.append(printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()));
      s.append(".");
      s.append(comp.getOuterComponent().get().getName());
      s.append("gen");
    }
    else {
      s.append("package ");
      s.append(comp.getPackageName());
    }
    return s.toString();
  }

  /**
   * Helper function used to determine package names.
   */
  public static String printPackageWithoutKeyWordAndSemicolon(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    if (comp.isInnerComponent() && comp.getOuterComponent().isPresent()) {
      return printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "." + comp
        .getOuterComponent().get().getName() + "gen";
    }
    else {
      return comp.getPackageName();
    }
  }

  public static String printSuperClassFQ(ComponentTypeSymbol comp) {
    String packageName = printPackageWithoutKeyWordAndSemicolon(comp.getParent());
    if (packageName.equals("")) {
      return comp.getParent().getName();
    }
    else {
      return packageName + "." + comp.getParent().getName();
    }
  }

  public static String printNamespaceStart(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    List<String> packages = ComponentHelper.getPackages(comp);
    packages = packages.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    s.append("namespace montithings {\n");
    for (String pack : packages) {
      s.append("namespace ");
      s.append(pack);
      s.append(" {\n");
    }
    return s.toString();
  }

  public static String printNamespaceEnd(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    List<String> packages = ComponentHelper.getPackages(comp);
    packages = packages.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    for (int i = 0; i < packages.size(); i++) {
      s.append("} // namespace ");
      s.append(packages.get(packages.size() - (i + 1)));
      s.append("\n");
    }
    s.append("} // namespace montithings\n");
    return s.toString();
  }

  public static String printGetPort(ASTPortAccess access) {
    StringBuilder s = new StringBuilder();
    if (access.isPresentComponent()) {
      s.append(access.getComponent()).append(".");
    }
    else {
      s.append("this->");
    }
    s.append("getInterface ()->");
    s.append("getPort");
    s.append(StringUtils.capitalize(access.getPort()));
    s.append("()");
    return s.toString();
  }

  public static String printComponentPrefix(ASTPortAccess access){
    StringBuilder s = new StringBuilder();
    if (access.isPresentComponent()) {
      s.append(access.getComponent());
      s.append(".");
    }
    else {
      s.append("this->");
    }
    return s.toString();
  }

  public static String printExpression(ASTExpression expr, boolean isAssignment) {
    return CppPrettyPrinter.print(expr);
  }

  public static String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
  }

  public static String printSIExpression(ASTExpression expr, SymTypeExpression type){
    TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
    SymTypeExpression exprType = tc.typeOf(expr);
    return MontiThingsSIUnitLiteralsPrettyPrinter
      .factorStart(MontiThingsSIUnitLiteralsPrettyPrinter.getSIConverter(type, exprType))
            + printExpression(expr, true) +
            MontiThingsSIUnitLiteralsPrettyPrinter
              .factorEnd(MontiThingsSIUnitLiteralsPrettyPrinter.getSIConverter(type, exprType));
  }

  public static String printIncludes(ComponentTypeSymbol comp, ConfigParams config) {
    StringBuilder s = new StringBuilder();
    HashSet<String> portIncludes = new HashSet<>();
    HashSet<ASTCDEImportStatement> includeStatements = new HashSet<>();
    List<ImportStatement> imports = ComponentHelper.getImports(comp);

    StringBuilder escape = new StringBuilder();
    int escapeCount = 1 + StringUtils.countMatches(comp.getPackageName(), '.');
    for (int i = 0; i < escapeCount; i++) {
      escape.append("../");
    }

    // Generated interfaces for dynamics
    if (!comp.getPorts().isEmpty()) {
      String componentImportStatement = getComponentInterfaceImport(comp.getName(),
        escape.toString());
      s.append(componentImportStatement).append("\n");

      for (String interfaceName : ComponentHelper.getInterfaceClassNames(comp, false)) {
        String interfaceImportStatement = getComponentInterfaceImport(interfaceName,
          escape.toString());
        s.append(interfaceImportStatement).append("\n");
      }
    }

    for (ImportStatement imp : imports) {
      // Skip imports that import enum constants
      Optional<TypeSymbol> type = comp.getEnclosingScope().resolveType(imp.getStatement());
      if (type.isPresent() && ((OOTypeSymbol) type.get()).isIsEnum() && imp.isStar()) {
        continue;
      }

      // Skip components. Will be added later
      Optional<ComponentTypeSymbol> compType = comp.getEnclosingScope()
        .resolveComponentType(imp.getStatement());
      if (compType.isPresent()) {
        continue;
      }

      String importStatement = "#include \""
        + escape
        + imp.getStatement().replaceAll("\\.", "/")
        + (imp.isStar() ? "/Package.h" : ".h")
        + "\"";
      s.append(importStatement).append("\n");
    }


    for (PortSymbol port : comp.getPorts()) {
      if (TypesHelper.portUsesCdType(port)) {
        Optional<ASTCDEImportStatement> cdeImportStatementOpt = TypesHelper
          .getCDEReplacement(port, config);
        String portNamespace = TypesPrinter.printCdPortFQN(comp, port, config);
        if (cdeImportStatementOpt.isPresent()) {
          includeStatements.add(cdeImportStatementOpt.get());
          String[] adapterName = portNamespace.split("::");
          if (adapterName.length >= 2) {
            portIncludes.add("#include \"" + adapterName[adapterName.length - 2] + "Adapter.h\"");
          }
        }
        else {
          portIncludes.add("#include \"" + escape + portNamespace.replace("::", "/") + ".h\"");
        }
      }
    }
    for (String include : portIncludes) {
      s.append(include).append("\n");
    }

    s.append(printIncludes(escape.toString(), Lists.newArrayList(includeStatements)));
    return s.toString();
  }

  public static String getComponentInterfaceImport(String interfaceName, String escape) {
    return "#include \""
      + escape
      + ClassDiagramUtil.COMPONENT_TYPE_PREFIX + interfaceName + "/"
      + ClassDiagramUtil.COMPONENT_TYPE_PREFIX + interfaceName + ".h"
      + "\"";
  }

  public static String printIncludes(ComponentTypeSymbol comp, String compname,
    ConfigParams config) {
    Set<String> compIncludes = new HashSet<>();
    for (ComponentInstanceSymbol subcomponent : comp.getSubComponents()) {
      if (!getGenericParameters(comp).contains(subcomponent.getGenericType().getName())) {
        boolean isInner = subcomponent.getType().isInnerComponent();

        if (config.getSplittingMode() == ConfigParams.SplittingMode.OFF || isInner){
          compIncludes.add("#include \"" + ComponentHelper.getPackagePath(comp, subcomponent)
            + (isInner ? (comp.getName() + "-Inner/") : "")
            + ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config, false)
            + ".h\"");
        } else {
          if(ComponentHelper.shouldIncludeSubcomponents(comp, config)) {
            compIncludes.add("#include <"
                + subcomponent.getType().getName().replaceAll("\\.", "/")
                + ".h>");
          }
        }
      }
      Set<String> genericIncludes = ComponentHelper.includeGenericComponent(comp, subcomponent);
      for (String genericInclude : genericIncludes) {
        compIncludes.add("#include \"" + genericInclude + ".h\"");
      }
    }
    StringBuilder s = new StringBuilder();
    for (String include : compIncludes) {
      s.append(include).append("\n");
    }
    s.append("#include \"").append(compname).append("Input.h\"\n");
    s.append("#include \"").append(compname).append("Result.h\"\n");
    return s.toString();
  }

  public static String escapePackage(List<String> packageName) {
    StringBuilder result = new StringBuilder();
    for (String ignored : packageName) {
      result.append("../");
    }
    return result.toString();
  }

  public static String printIncludes(String prefix, List<ASTCDEImportStatement> imports) {
    HashSet<String> portIncludes = new HashSet<>();
    for (ASTCDEImportStatement importStatement : imports) {
      String portPackage = importStatement.getImportSource().toString();
      portIncludes.add("#include " + portPackage);
      portIncludes.add("#include \"" + prefix + printCDType(importStatement)
        .replaceFirst("montithings::", "")
        .replace("::", "/")
        + ".h\"");
    }
    StringBuilder s = new StringBuilder();
    for (String include : portIncludes) {
      s.append(include).append("\n");
    }
    return s.toString();
  }

  public static String printCDType(ASTCDEImportStatement importStatement) {
    String namespace = "montithings::";
    Optional<TypeSymbol> ts = importStatement.getSymbol().getEnclosingScope()
        .resolveType(importStatement.getCdType().getQName());
    if (!ts.isPresent()) {
      System.err.println(Arrays.toString(new Throwable().getStackTrace()));
      Log.error("CDType '" + importStatement.getCdType().getQName() + "' not present");
      System.exit(-1); // unreachable, but makes the static analyzer happy
    }
    String importType = ts.get().getFullName().replace(".", "::");
    return namespace + importType;
  }

  public static String printPackageNamespace(ComponentTypeSymbol comp,
    ComponentInstanceSymbol subcomp) {
    ComponentTypeSymbol subcomponentType = subcomp.getType();
    String fullNamespaceSubcomponent = ComponentHelper
      .printPackageNamespaceForComponent(subcomponentType);
    String fullNamespaceEnclosingComponent = ComponentHelper
      .printPackageNamespaceForComponent(comp);
    if (!fullNamespaceSubcomponent.equals(fullNamespaceEnclosingComponent) &&
      fullNamespaceSubcomponent.startsWith(fullNamespaceEnclosingComponent)) {
      return fullNamespaceSubcomponent.split(fullNamespaceEnclosingComponent)[1];
    }
    else {
      return fullNamespaceSubcomponent;
    }
  }
}

