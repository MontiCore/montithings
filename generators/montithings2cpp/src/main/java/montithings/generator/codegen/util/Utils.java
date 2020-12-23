// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.util;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
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
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montithings._ast.ASTMTComponentType;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.CppPrettyPrinter;
import montithings.generator.helper.TypesHelper;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import java.util.*;

public class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  public static String printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    int i = 1;
    for (VariableSymbol param : comp.getParameters()) {
      s.append(TypesHelper.java2cppTypeString(param.getType().print()) + " " + param.getName());
      if (i != comp.getParameters().size()) {
        s.append(',');
      }
      i++;
    }
    return s.toString();
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
    return type + " " + name + "=" + initialValue + ";";
  }

  public static String printMember(String type, String name) {
    return type + " " + name + ";";
  }

  /**
   * Prints members for configuration parameters.
   */
  public static String printConfigParameters(ComponentTypeSymbol comp, ConfigParams config) {
    StringBuilder s = new StringBuilder();
    for (VariableSymbol param : comp.getParameters()) {
      if (param.getAstNode() instanceof ASTArcParameter) {
        ASTArcParameter parameter = (ASTArcParameter) param.getAstNode();
        s.append(printMember(ComponentHelper.printCPPTypeName(param.getType(), comp, config),
          param.getName(), printExpression(parameter.getDefault())));
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
    StringBuilder s = new StringBuilder();
    for (VariableSymbol variable : ComponentHelper.getFields(comp)) {
      if (variable.getAstNode() instanceof ASTArcField) {
        ASTArcField field = (ASTArcField) variable.getAstNode();
        s.append(
          printMember(ComponentHelper.printCPPTypeName(variable.getType(), comp, config),
            variable.getName(), printExpression(field.getInitial())));
      }
      else {
        s.append(
          printMember(ComponentHelper.printCPPTypeName(variable.getType(), comp, config),
            variable.getName()));
      }
    }
    return s.toString();
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

  public static String printExpression(ASTExpression expr, boolean isAssignment) {
    return CppPrettyPrinter.print(expr);
  }

  public static String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
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
        if (cdeImportStatementOpt.isPresent()) {
          includeStatements.add(cdeImportStatementOpt.get());
          String portNamespace = ComponentHelper.printCdPortFQN(comp, port, config);
          String[] adapterName = portNamespace.split("::");
          if (adapterName.length >= 2) {
            portIncludes.add("#include \"" + adapterName[adapterName.length - 2] + "Adapter.h\"");
          }
        }
        else {
          String portNamespace = ComponentHelper.printCdPortFQN(comp, port, config);
          portIncludes.add("#include \"" + portNamespace.replace("::", "/") + ".h\"");
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
      if (!getGenericParameters(comp).contains(subcomponent.getType().getName())) {
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

