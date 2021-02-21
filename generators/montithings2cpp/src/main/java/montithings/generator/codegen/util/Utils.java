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
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montithings._ast.ASTMTComponentType;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.CppPrettyPrinter;
import montithings.generator.helper.TypesHelper;
import montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Preconditions;

import java.util.*;

public class Utils {

  public static String getDeployFile(ComponentTypeSymbol comp) {
    String filename = comp.getFullName();
    int indexComponentName = filename.length() - comp.getName().length();
    filename = filename.substring(0, indexComponentName);
    filename = filename.replaceAll("\\.", "/");
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
      s.append(TypesHelper.java2cppTypeString(type.print()) + " " + param.getName());
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

    String result = "";
    for (int i = 0 ; i < params.size() ; i++) {
      result += "config[\"" + params.get(i).getName() + "\"]"
        + " = dataToJson (" + paramValues.get(i) + ");\n";
    }
    return result;
  }

  public static String printSIParameters(ComponentTypeSymbol comp, ComponentInstanceSymbol compInstance){
    String result = "";
    for (PortSymbol ps : compInstance.getType().getAllPorts()){
      if (ComponentHelper.isSIUnitPort(ps) && ps.isIncoming()){
        for (ASTConnector c : comp.getAstNode().getConnectors()){
          for (ASTPortAccess portAccess : c.getTargetList()){
            Optional<PortSymbol> ops = ComponentHelper.getPortSymbolFromPortAccess(portAccess);
            if(ops.isPresent() && ops.get().equals(ps)){
              result += "config[\"" + ps.getName() + "ConversionFactor\"]" + " = dataToJson (" + ComponentHelper.
                getConversionFactorFromSourceAndTarget(c.getSource(), portAccess) + ");\n";
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * Prints the component's imports
   */
  public static String printImports(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    for (ImportStatement _import : ComponentHelper.getImports(comp)) {
      s.append("import " + _import.getStatement());
      if (_import.isStar()) {
        s.append(".*");
      }
    }
    for (ComponentTypeSymbol inner : comp.getInnerComponents()) {
      s.append("import " + printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.getName());
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
          s.append(printMember(ComponentHelper.printCPPTypeName(param.getType(), comp, config),
                  param.getName(), printSIExpression(parameter.getDefault(), param.getType())));
        }
        else {
          s.append(printMember(ComponentHelper.printCPPTypeName(param.getType(), comp, config),
                  param.getName(), printExpression(parameter.getDefault())));
        }
      }
      else {
        s.append(printMember(ComponentHelper.printCPPTypeName(param.getType(), comp, config),
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
      String typeName = ComponentHelper.printCPPTypeName(variable.getType(), comp, config);
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
    if (comp.getAstNode().getHead() instanceof ASTGenericComponentHead &&
      !((ASTGenericComponentHead) comp.getAstNode().getHead()).isEmptyArcTypeParameters()) {
      return true;
    }
    return false;
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
    if (comp.isInnerComponent()) {
      s.append(
        "package " + printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "."
          + comp.getOuterComponent().get().getName() + "gen");
    }
    else {
      s.append("package " + comp.getPackageName());
    }
    return s.toString();
  }

  /**
   * Helper function used to determine package names.
   */
  public static String printPackageWithoutKeyWordAndSemicolon(
    arcbasis._symboltable.ComponentTypeSymbol comp) {
    if (comp.isInnerComponent()) {
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
    s.append("namespace montithings {\n");
    for (String pack : packages) {
      s.append("namespace " + pack + " {\n");
    }
    return s.toString();
  }

  public static String printNamespaceEnd(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    List<String> packages = ComponentHelper.getPackages(comp);
    for (int i = 0; i < packages.size(); i++) {
      s.append("} // namespace " + packages.get(packages.size() - (i + 1)));
      s.append("\n");
    }
    s.append("} // namespace montithings\n");
    return s.toString();
  }

  public static String printGetPort(ASTPortAccess access) {
    StringBuilder s = new StringBuilder();
    if (access.isPresentComponent()) {
      s.append(access.getComponent() + ".");
    }
    else {
      s.append("this->");
    }
    s.append("getPort" + StringUtils.capitalize(access.getPort()) + "()");
    return s.toString();
  }

  public static String printComponentPrefix(ASTPortAccess access){
    StringBuilder s = new StringBuilder();
    if (access.isPresentComponent()) {
      s.append(access.getComponent() + ".");
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

    String escape = "";
    int escapeCount = 1 + StringUtils.countMatches(comp.getPackageName(), '.');
    for (int i = 0; i < escapeCount; i++) {
      escape += "../";
    }

    for (ImportStatement imp : imports) {
      // Skip imports that import enum constants
      Optional<TypeSymbol> type = comp.getEnclosingScope().resolveType(imp.getStatement());
      if (type.isPresent() && ((OOTypeSymbol) type.get()).isIsEnum() && imp.isStar()) {
        continue;
      }

      // Skip interface components. We dont generate code for them, there's nothing to import here
      Optional<ComponentTypeSymbol> compType = comp.getEnclosingScope()
        .resolveComponentType(imp.getStatement());
      if (compType.isPresent() && ((ASTMTComponentType) compType.get().getAstNode())
        .getMTComponentModifier().isInterface()) {
        continue;
      }

      String importStatement = "#include \""
        + escape
        + imp.getStatement().replaceAll("\\.", "/")
        + (imp.isStar() ? "/Package.h" : ".h")
        + "\"";
      s.append(importStatement + "\n");
    }


    for (PortSymbol port : comp.getPorts()) {
      if (ComponentHelper.portUsesCdType(port)) {
        Optional<ASTCDEImportStatement> cdeImportStatementOpt = ComponentHelper
          .getCDEReplacement(port, config);
        String portNamespace = ComponentHelper.printCdPortFQN(comp, port, config);
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
      s.append(include + "\n");
    }

    s.append(printIncludes(escape, Lists.newArrayList(includeStatements)));
    return s.toString();
  }

  public static String printIncludes(ComponentTypeSymbol comp, String compname,
    ConfigParams config) {
    Set<String> compIncludes = new HashSet<String>();
    for (ComponentInstanceSymbol subcomponent : comp.getSubComponents()) {
      if (!getGenericParameters(comp).contains(subcomponent.getGenericType().getName())) {
        boolean isInner = subcomponent.getType().isInnerComponent();
        compIncludes.add("#include \"" + ComponentHelper.getPackagePath(comp, subcomponent)
          + (isInner ? (comp.getName() + "-Inner/") : "")
          + ComponentHelper.getSubComponentTypeNameWithoutPackage(subcomponent, config, false)
          + ".h\"");
      }
      Set<String> genericIncludes = ComponentHelper.includeGenericComponent(comp, subcomponent);
      for (String genericInclude : genericIncludes) {
        compIncludes.add("#include \"" + genericInclude + ".h\"");
      }
    }
    StringBuilder s = new StringBuilder();
    for (String include : compIncludes) {
      s.append(include + "\n");
    }
    s.append("#include \"" + compname + "Input.h\"\n");
    s.append("#include \"" + compname + "Result.h\"\n");
    return s.toString();
  }

  public static String escapePackage(List<String> packageName) {
    String result = "";
    for (String ignored : packageName) {
      result += "../";
    }
    return result;
  }

  public static String printIncludes(String prefix, List<ASTCDEImportStatement> imports) {
    HashSet<String> portIncludes = new HashSet<String>();
    for (ASTCDEImportStatement importStatement : imports) {
      String portPackage = importStatement.getImportSource().toString();
      portIncludes.add("#include " + portPackage);
      portIncludes.add("#include \"" + prefix + printCDType(importStatement).replaceFirst("montithings::", "").replaceAll("::", "/") + ".h\"");
    }
    StringBuilder s = new StringBuilder();
    for (String include : portIncludes) {
      s.append(include + "\n");
    }
    return s.toString();
  }

  public static String printCDType(ASTCDEImportStatement importStatement) {
    StringBuilder namespace = new StringBuilder("montithings::");
    String importType = importStatement.getSymbol().getEnclosingScope()
      .resolveType(importStatement.getCdType().getQName()).get()
      .getFullName()
      .replaceAll("\\.", "::");
    return namespace + importType;
  }

  public static String printPackageNamespace(ComponentTypeSymbol comp,
    ComponentInstanceSymbol subcomp) {
    // TODO: getTypeInfo statt getType hat früher hier Generics aufgelöst
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

