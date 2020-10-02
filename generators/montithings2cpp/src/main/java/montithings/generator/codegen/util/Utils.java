// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.util;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import cdlangextension._ast.ASTCDEImportStatement;
import com.google.common.base.Strings;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.CppPrettyPrinter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Utils {

  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  public static String printConfigurationParametersAsList(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    int i=1;
      for(FieldSymbol param : comp.getParameters()){
        s.append(param.getType().print() + param.getName());
        if(i!=comp.getParameters().size()) {
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
      for(ImportStatement _import : ComponentHelper.getImports(comp)){
        s.append("import "+_import.getStatement());
        if(_import.isStar()) {
          s.append(".*");
        }
      }
      for(ComponentTypeSymbol inner : comp.getInnerComponents()){
        s.append("import "+ printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.getName());
      }
    return s.toString();
  }

  /**
   * Prints a member of given visibility name and type
   */
  public static String printMember(String type, String name) {
    return type + " " + name;
  }

  /**
   * Prints members for configuration parameters.
   */
  public static String printConfigParameters(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
      for(FieldSymbol param : comp.getParameters()){
        s.append(printMember(ComponentHelper.printCPPTypeName(param.getType()), param.getName()));
      }
    return s.toString();
  }

  /**
   * Prints members for variables
   */
  public static String printVariables(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
      for(FieldSymbol variable : ComponentHelper.getFields(comp)){
        s.append(printMember(ComponentHelper.printCPPTypeName(variable.getType()), variable.getName()));
      }
    return s.toString();
  }

  /**
   * Prints formal parameters of a component.
   */
  public static String printFormalTypeParameters(ComponentTypeSymbol comp) {
    return printFormalTypeParameters(comp, false);
  }

  public static String printFormalTypeParameters(ComponentTypeSymbol comp, Boolean withClassPrefix) {
    StringBuilder s = new StringBuilder();
    int i = 1;
    if (comp.hasTypeParameter()) {
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
    if( comp.hasTypeParameter()) {
      return "template"+printFormalTypeParameters(comp, true);
    }
    return "";
  }

  private static List<String> getGenericParameters(ComponentTypeSymbol comp) {
    List<String> output = new ArrayList<>();
    if (comp.hasTypeParameter()) {
      List<TypeVarSymbol> parameterList = comp.getTypeParameters();
      for (TypeVarSymbol typeParameter : parameterList) {
        output.add(typeParameter.getName());
      }
    }
    return output;
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
      s.append("package " + printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "." + comp.getOuterComponent().get().getName() + "gen");
    }
    else {
      s.append("package " + comp.getPackageName());
    }
    return s.toString();
  }

  /**
   * Helper function used to determine package names.
   */
  public static String printPackageWithoutKeyWordAndSemicolon(arcbasis._symboltable.ComponentTypeSymbol comp) {
    if (comp.isInnerComponent()) {
      return printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "." + comp.getOuterComponent().get().getName() + "gen";
    }
    else {
      return comp.getPackageName();
    }
  }

  public static String printSuperClassFQ(ComponentTypeSymbol comp){
    String packageName = printPackageWithoutKeyWordAndSemicolon(comp.getParentInfo());
    if(packageName.equals("")){
      return comp.getParent().getName();
    } else {
      return packageName+"."+comp.getParent().getName();
    }
  }

  public static String printNamespaceStart(ComponentTypeSymbol comp) {
    StringBuilder s = new StringBuilder();
    List<String> packages = ComponentHelper.getPackages(comp);
    s.append("namespace montithings { \n ");
    for(String pack : packages){
      s.append("namespace "+ pack);
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
    if( access.isPresentComponent()) {
      s.append(access.getComponent() + ".");
    }
    else{
        s.append("this->");
    }
      s.append("getPort"+ StringUtils.capitalize(access.getPort()) +"()");
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
    HashSet<String> portIncludes = new HashSet<String>();
    HashSet<ASTCDEImportStatement> includeStatements = new HashSet<ASTCDEImportStatement>();
    for (port : comp.getPorts()) {
      if (ComponentHelper.portUsesCdType(port)) {
        cdeImportStatementOpt = ComponentHelper.getCppImportExtension(port, config);
        if(cdeImportStatementOpt.isPresent()) {
          includeStatements.add(cdeImportStatementOpt.get());
          portNamespace = ComponentHelper.printCdPortPackageNamespace(comp, port, config)
          adapterName = portNamespace.split("::");
          if(adapterName.length>=2) {
            portIncludes.add('''#include "«adapterName.get(adapterName.length-2)»Adapter.h"''')
          }
        }
        else{
          portNamespace = ComponentHelper.printCdPortPackageNamespace(comp, port, config)
          portIncludes.add('''#include "«portNamespace.replace("::", "/")».h"''')
        }
      }
    }
    for(include : portIncludes){
      s.append(include+"\n");
    }
    s.append(Adapter.printIncludes(includeStatements.toList));
    return s;
  }

  def static String printIncludes(List<ASTCDEImportStatement> imports) {
    var HashSet<String> portIncludes = new HashSet<String>()
    for (importStatement : imports) {
      var portPackage = importStatement.getImportSource().toString();
      portIncludes.add('''#include «portPackage»''');
      portIncludes.add('''#include "«printCDType(importStatement).replace("::", "/")».h"''')
    }
    return '''
	«FOR include : portIncludes»
	«include»
	«ENDFOR»
    '''
  }

  def static String printCDType(ASTCDEImportStatement importStatement) {
    var namespace = new StringBuilder("montithings::");
    if(importStatement.isPresentPackage()){
      var packages = importStatement.getPackage().getPartList();

      for (String packageName : packages) {
        namespace.append(packageName).append("::");
      }
      return ComponentHelper.printPackageNamespaceFromString(packages.get(packages.size-1)+"::"+importStatement.getName().toString(), namespace.toString());
    }
    return importStatement.getName().toString();
  }

}

