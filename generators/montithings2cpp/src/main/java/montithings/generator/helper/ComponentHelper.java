// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._ast.*;
import arcbasis._symboltable.*;
import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.collect.FluentIterable;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.StringTransformations;
import montiarc._ast.ASTArcSync;
import montiarc._ast.ASTArcTiming;
import montiarc._symboltable.MontiArcArtifactScope;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings.generator.codegen.xtend.util.Utils;
import portextensions._ast.ASTAnnotatedPort;
import portextensions._ast.ASTBufferedPort;
import portextensions._ast.ASTSyncStatement;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;
import prepostcondition.helper.ExpressionUtil;
import prepostcondition.visitor.GuardExpressionVisitor;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 */
public class ComponentHelper {
  private final ComponentTypeSymbol component;

  protected final ASTComponentType componentNode;

  public ComponentHelper(ComponentTypeSymbol component) {
    this.component = component;
    if (component.isPresentAstNode()) {
      componentNode = (ASTComponentType) component.getAstNode();
    }
    else {
      componentNode = null;
    }
  }

  public static List<ImportStatement> getImports(ComponentTypeSymbol symbol) {
    while (symbol.getOuterComponent().isPresent()) {
      symbol = symbol.getOuterComponent().get();
    }
    ASTComponentType ast = symbol.getAstNode();
    return ((MontiArcArtifactScope) ast.getEnclosingScope()).getImportList();
  }

  public static String printCPPTypeName(SymTypeExpression expression) {
    return expression.print();
  }

  /**
   * @return A list of String representations of the actual type arguments
   * assigned to the super component
   */
  public List<String> getSuperCompActualTypeArguments() {
    // TODO: Write me
    final List<String> paramList = new ArrayList<>();
    if (component.isPresentParentComponent()) {
      final ComponentTypeSymbolLoader componentSymbolReference = component.getParent();
/*      final List<ActualTypeArgument> actualTypeArgs = componentSymbolReference
        .getActualTypeArguments();
      String componentPrefix = this.component.getFullName() + ".";
      for (ActualTypeArgument actualTypeArg : actualTypeArgs) {
        final String printedTypeArg = SymbolPrinter.printTypeArgument(actualTypeArg);
        if (printedTypeArg.startsWith(componentPrefix)) {
          paramList.add(printedTypeArg.substring(componentPrefix.length()));
        } else {
          paramList.add(printedTypeArg);
        }
      }*/
    }
    return paramList;
  }

  /**
   * Set of components used as generic type argument as include string
   *
   * @param comp     component that gets the new includes
   * @param instance component instance that assigns component to generic
   * @return Set of components used as generic type argument as include string.
   * Is empty if include is not needed.
   */
  public static Set<String> includeGenericComponent(ComponentTypeSymbol comp,
      ComponentInstanceSymbol instance) {
    /*
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    if (componentTypeReference.hasActualTypeArguments()) {
      List<ActualTypeArgument> types = new ArrayList<>(
          componentTypeReference.getActualTypeArguments());
      return includeGenericComponentIterate(comp, instance, types);
    }
    */
    return new HashSet<>();
  }

  public static boolean portUsesCdType(PortSymbol portSymbol) {
    // TODO: Write me
    return false;
  }

  public static String printCdPortPackageNamespace(ComponentTypeSymbol componentSymbol,
      PortSymbol portSymbol) {
    //TODO: Write me
    return "";
  }

  /**
   * Prints the type of the given port respecting inherited ports and the actual
   * type values
   *
   * @param port Symbol of the port of which to determine the type
   * @return The string representation of the type
   */
  public String getRealPortTypeString(PortSymbol port) {
    return getRealPortTypeString(this.component, port);
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This
   * takes in to account whether the port is inherited and possible required
   * renamings due to generic type parameters and their actual arguments.
   *
   * @param ComponentTypeSymbol Symbol of the component which contains the port
   * @param portSymbol          Symbol of the port for which the type name should be
   *                            determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentTypeSymbol ComponentTypeSymbol,
      PortSymbol portSymbol) {
    return portSymbol.getType().print();
  }

  public static String printPackageNamespace(ComponentTypeSymbol comp, CDTypeSymbol cdtype) {
    String fullNamespaceSubcomponent = "montithings::" + cdtype.getFullName().replace(".", "::");
    String fullNamespaceEnclosingComponent = printPackageNamespaceForComponent(comp);
    if (!fullNamespaceSubcomponent.equals(fullNamespaceEnclosingComponent) &&
        fullNamespaceSubcomponent.startsWith(fullNamespaceEnclosingComponent)) {
      return fullNamespaceSubcomponent.split(fullNamespaceEnclosingComponent)[1];
    }
    else {
      return fullNamespaceSubcomponent;
    }
  }

  public static boolean isInterfaceComponent(ComponentTypeSymbol comp) {
    if (comp.getAstNode() instanceof ASTMTComponentType) {
      ASTMTComponentType astmtComponentType = (ASTMTComponentType) comp.getAstNode();
      return astmtComponentType.getMTComponentModifier().isInterface();
    }
    return false;
  }

  public static String printPackageNamespaceForComponent(ComponentTypeSymbol comp) {
    List<String> packages = ComponentHelper.getPackages(comp);
    if (isInterfaceComponent(comp)) {
      return "";
    }
    StringBuilder namespace = new StringBuilder("montithings::");
    for (String packageName : packages) {
      namespace.append(packageName).append("::");
    }
    return namespace.toString();
  }

  private static final HashMap<String, String> PRIMITIVE_TYPES = new HashMap<String, String>() {
    {
      put("int", "Integer");
      put("double", "Double");
      put("boolean", "Boolean");
      put("byte", "Byte");
      put("char", "Character");
      put("long", "Long");
      put("float", "Float");
      put("short", "Short");
    }
  };

  /**
   * Boxes datatype if applicable.
   *
   * @param datatype String representation of the datatype to box.
   * @return The boxed datatype.
   */
  public static String autobox(String datatype) {
    String[] tokens = datatype.split("\\b");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tokens.length; i++) {
      if (PRIMITIVE_TYPES.containsKey(tokens[i])) {
        tokens[i] = autoboxType(tokens[i]);
      }
      sb.append(tokens[i]);
    }
    return sb.toString();
  }

  private static String autoboxType(String datatype) {
    String autoBoxedTypeName = datatype;
    if (PRIMITIVE_TYPES.containsKey(datatype)) {
      autoBoxedTypeName = PRIMITIVE_TYPES.get(datatype);
    }
    return autoBoxedTypeName;
  }

  /**
   * Calculates the values of the parameters of a {@link ComponentInstanceSymbol}.
   * This takes default values for parameters into account and adds them as
   * required. Default values are only added from left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instanciated with parameters <code>"Test String", 5</code> this
   * method adds <code>new Object()</code> as the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters
   *              should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    List<ASTExpression> configArguments = param.getArguments();
    ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());

    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<FieldSymbol> configParameters = param.getType().getLoadedSymbol().getParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponentType astNode = param.getType().getLoadedSymbol().getAstNode();
      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTArcParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = printer.prettyprint(astParameter.getDefault());
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }

    return outputParameters;
  }

  public static String printTypeArguments(List<TypeVarSymbol> types) {
    String result = "";
    List<String> typeNames = new ArrayList<>();
    for (TypeVarSymbol type : types) {
      typeNames.add(type.getName());
    }
    return String.join(", ", typeNames);
  }

  /**
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    final ComponentTypeSymbolLoader componentTypeReference = instance.getType();

    String packageName = Utils.printPackageWithoutKeyWordAndSemicolon(
        componentTypeReference.getLoadedSymbol());
    if (!packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentTypeReference.getName();
    if (componentTypeReference.getLoadedSymbol().hasTypeParameter()) {
      result += printTypeArguments(componentTypeReference.getLoadedSymbol().getTypeParameters());
    }
    return result;
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation) {
    return getSubComponentTypeNameWithoutPackage(instance, interfaceToImplementation, true);
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation, boolean printTypeParameters) {
    String result = "";
    final ComponentTypeSymbolLoader componentTypeReference = instance.getType();
    result += componentTypeReference.getName();
    if (componentTypeReference.getLoadedSymbol().hasTypeParameter() && printTypeParameters) {
      // format simple component type name to full component type name
      List<TypeVarSymbol> types = new ArrayList<>(
          componentTypeReference.getLoadedSymbol().getTypeParameters());
      //TODO: we probably still need the following call?
      //types = addTypeParameterComponentPackage(instance, types);
      result += printTypeArguments(types);
    }
    if (interfaceToImplementation.containsKey(result)) {
      return interfaceToImplementation.get(result);
    }
    return result;
  }


  /**
   * Replace subcomponent instance type with generic if it is an interface type.
   * component top<T extends InterfaceComponent>{
   * component InterfaceComponent xy;
   * }
   * results in
   * component top<T extends InterfaceComponent>{
   * component T xy;
   * }
   *
   * @param comp                      component containing the subcomponent instances.
   * @param instance                  the instance where it's type may be replaced.
   * @param interfaceToImplementation binding which replaces an interface type if no generic is used.
   * @return the subcomponent type name without package.
   */

  public static String getSubComponentTypeNameWithBinding(ComponentTypeSymbol comp,
      ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation) {
    return instance.getType().getLoadedSymbol().getName();
    //TODO: Implement me
    /*
    HashMap<String, String> interfaceToImplementationGeneric = new HashMap<>(
        interfaceToImplementation);
    final ComponentTypeSymbolLoader componentTypeReference = instance.getType();
    // check if needed optional values are present and if the instance component type is an interface
    if (componentTypeReference.isSymbolLoaded()
        && componentTypeReference.getLoadedSymbol().getAstNode()
        instanceof ASTMTComponentType) {
      ASTMTComponentType interfaceComp = (ASTMTComponentType) componentTypeReference
          .getLoadedSymbol().getAstNode();
      if (interfaceComp.getMTComponentModifier().isInterface()) {
        // get interface component type name and the replacing generic name.
        String interfaceCompName = interfaceComp.getName();
        if (comp.isPresentAstNode()) {
          ASTComponentType compBind = comp.getAstNode();
          String typeName = GenericBindingUtil.getSubComponentType(compBind, instance);
          // replace the interface component type of the instance with the generic type.
          if (typeName != null && interfaceCompName != null && !interfaceCompName
              .equals(typeName)) {
            interfaceToImplementationGeneric.remove(interfaceCompName);
            interfaceToImplementationGeneric.put(interfaceCompName, typeName);
          }
        }
      }
    }
    return getSubComponentTypeNameWithoutPackage(instance, interfaceToImplementationGeneric);
     */
  }

  /**
   * Determine whether the port of the given connector is an incoming or outgoing
   * port.
   *
   * @param cmp      The component defining the connector
   * @param portAccess the portaccess to evaluate
   * @return true, if the port is an incoming port. False, otherwise.
   */
  public static boolean isIncomingPort(ComponentTypeSymbol cmp, ASTPortAccess portAccess) {
    Optional<PortSymbol> port;
    String portNameUnqualified = portAccess.getPort();
    // port is of subcomponent
    if (portAccess.isPresentComponent()) {
      String subCompName = portAccess.getComponent();
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
          .resolveComponentInstance(subCompName);
      ComponentTypeSymbol subComp = subCompInstance.get().getType().getLoadedSymbol();
      port = subComp.getSpannedScope().resolvePort(portNameUnqualified);
    }
    else {
      port = cmp.getSpannedScope().resolvePort(portNameUnqualified);
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }

  /**
   * @return Corresponding CPP types from input java types
   */
  public static String java2cppTypeString(String type) {
    String replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    while (!type.equals(replacedArray)) {
      type = replacedArray;
      replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    }
    type = type.replaceAll("String", "std::string");
    type = type.replaceAll("Integer", "int");
    type = type.replaceAll("Map", "std::map");
    type = type.replaceAll("Set", "std::set");
    type = type.replaceAll("List", "std::list");
    type = type.replaceAll("Boolean", "bool");
    type = type.replaceAll("Character", "char");
    type = type.replaceAll("Double", "double");
    type = type.replaceAll("Float", "float");

    return type;
  }

  public String getRealPortCppTypeString(PortSymbol port) {
    return java2cppTypeString(getRealPortTypeString(port));

  }

  public static String getRealPortCppTypeString(ComponentTypeSymbol comp, PortSymbol port) {
    return java2cppTypeString(getRealPortTypeString(comp, port));
  }

  /**
   * @param hwcPath
   * @return Returns true if a handwritten implementation for the component exist
   */
  public static Boolean existsHWCClass(File hwcPath, String fqComponentName) {
    File ImplLocation = Paths.get(hwcPath.toString() + File.separator
        + fqComponentName.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".h")
        .toFile();
    return ImplLocation.isFile();
  }

  /**
   * @param hwcPath
   * @param comp
   * @param resourcePortName
   * @return Returns true if a handwritten implementation for the IPC Server exists
   */
  public static Boolean existsIPCServerHWCClass(File hwcPath, ComponentTypeSymbol comp,
      String resourcePortName) {
    String fqCompName = comp.getPackageName() + "." + comp.getName();
    File implLocation = Paths.get(hwcPath.toString() + File.separator
        + fqCompName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
        + "-" + StringTransformations.capitalize(resourcePortName) + File.separator
        + StringTransformations.capitalize(resourcePortName) + "Server.cpp").toFile();
    return implLocation.isFile();
  }

  protected static String getCalculationIntervalUnit(ComponentTypeSymbol comp) {
    return elementsOf(comp)
        .filter(ASTCalculationInterval.class)
        .first()
        .transform(e -> e.getTimeUnit().toString())
        .or("MSEC");
  }

  protected static int getCalculationInterval(ComponentTypeSymbol comp) {
    return elementsOf(comp)
        .filter(ASTCalculationInterval.class)
        .first()
        .transform(e -> e.getInterval().getValue())
        .or(50);
  }

  /**
   * Gets a string that corresponds to the update interval of the component in CPP code
   *
   * @param comp
   * @return CPP duration
   */
  public static String getExecutionIntervalMethod(ComponentTypeSymbol comp) {
    int interval = getCalculationInterval(comp);
    String intervalUnit = getCalculationIntervalUnit(comp);
    String method = "std::chrono::milliseconds(" + interval + ")";

    switch (intervalUnit) {
      case "MSEC":
        method = "std::chrono::milliseconds(" + interval + ")";
        break;
      case "SEC":
        method = "std::chrono::seconds(" + interval + ")";
        break;
      case "MIN":
        method = "std::chrono::seconds(" + interval * 60 + ")";

    }
    return method;
  }

  public static String getExecutionIntervalInMillis(ComponentTypeSymbol comp) {
    int interval = getCalculationInterval(comp);
    String intervalUnit = getCalculationIntervalUnit(comp);

    switch (intervalUnit) {
      case "MSEC":
        return "" + interval;
      case "SEC":
        return "" + interval * 1000;
      case "MIN":
        return "" + interval * 60 * 1000;
    }
    return "50";
  }

  /**
   * Returns true iff comp contains at least one buffered port
   */
  public static Boolean usesBatchMode(ComponentTypeSymbol comp) {
    // TODO: MontiArc 6
    return false;
    /*
        ((ASTComponentType) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .anyMatch(e -> e instanceof ASTBatchStatement);
     */
  }

  public static Boolean hasSyncGroups(ComponentTypeSymbol comp) {
    return getSyncGroups(comp).size() > 0;
  }

  /**
   * Returns all synchronization groups as lists of strings for easier code generation
   *
   * @param comp
   * @return
   */
  public static List<List<String>> getSyncGroups(ComponentTypeSymbol comp) {
    return elementsOf(comp)
        .filter(ASTSyncStatement.class)
        .transform(ASTSyncStatement::getSyncedPortList)
        .toList();
  }

  /**
   * Returns ports that don't appear in any synchronization group
   *
   * @param comp
   * @return
   */
  public static List<PortSymbol> getPortsNotInSyncGroup(ComponentTypeSymbol comp) {
    List<String> portsInSyncGroups = getSyncGroups(comp).stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    return comp.getAllIncomingPorts().stream()
        .filter(p -> !portsInSyncGroups.contains(p.getName()))
        .collect(Collectors.toList());
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   *
   * @param node
   * @return
   */
  public static List<ASTNameExpression> getGuardExpressionElements(ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor);
    return visitor.getExpressions();
  }

  /**
   * Get import for subpackages
   *
   * @param subPackagesPath
   * @return
   */
  public static String getSubPackageImports(File[] subPackagesPath) {
    String packageNames = "";
    String start = "\"./";
    String endCpp = "/*.cpp\"\n";
    String endH = "/*.h\"\n";

    for (File subPackage : subPackagesPath) {
      /**
       * Example of build String with 2 subpackages:
       *
       * \"./packageName1/*.cpp\"\n
       * \"./packageName1/*.h\"\n
       * \"./packageName2/*.cpp\"\n
       * \"./packageName2/*.h\"\n
       */
      packageNames += start + subPackage.getName() + endCpp;
      packageNames += start + subPackage.getName() + endH;
    }
    return packageNames;
  }

  public static String getSubPackageIncludes(File[] subPackagesPath) {
    String packageNames = "";
    String start = "include_directories(./";
    String end = ")\n";

    for (File subPackage : subPackagesPath) {
      /**
       * Example of build String with 2 subpackages:
       *
       * include_directories(./packageName1)\n
       * include_directories(./packageName2)\n
       */
      packageNames += start + subPackage.getName() + end;
    }
    return packageNames;
  }

  public static String getPackagePath(ComponentTypeSymbol comp, ComponentInstanceSymbol subComp) {
    return getPackagePath(comp, subComp.getType().getLoadedSymbol());
  }

  public static String getPackagePath(ComponentTypeSymbol comp, ComponentTypeSymbol subComp) {
    // Get package name of subcomponent
    String subCompPackageName = subComp.getPackageName();
    // Check if subcomponent is in different package than parent component
    if (!subCompPackageName.equals(comp.getPackageName())) {
      // Split packageName
      String[] path = subCompPackageName.split("\\.");
      // Build correct package path
      String correctPath = "";
      boolean leaveFirstOut = true;
      for (String dir : path) {
        if (leaveFirstOut) {
          leaveFirstOut = false;
          continue;
        }
        correctPath += dir + "/";
      }
      // Return correct path
      return correctPath;
    }
    // If subcomponent is in the same package as component, then no package path before class import required
    return "";
  }

  public static List<String> getPackages(ComponentTypeSymbol component) {
    String packageName = component.getPackageName();
    String[] packages = packageName.split("\\.");
    return Arrays.asList(packages);
  }

  /* ============================================================ */
  /* =================== MontiThings Adapter ==================== */
  /* ============================================================ */

  public static FluentIterable<ASTArcElement> elementsOf(ComponentTypeSymbol component) {
    return FluentIterable.from(component.getAstNode().getBody().getArcElementList());
  }

  public static List<ASTPrecondition> getPreconditions(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTPrecondition.class).toList();
  }

  public static List<ASTPostcondition> getPostconditions(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTPostcondition.class).toList();
  }

  public static List<ASTArcTiming> getTiming(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTArcTiming.class).toList();
  }

  public static boolean isTimesync(ComponentTypeSymbol component) {
    return getTiming(component).stream()
        .filter(e -> e.getArcTimeMode() instanceof ASTArcSync)
        .collect(Collectors.toSet()).size() > 0;
  }

  public static boolean hasBehavior(ComponentTypeSymbol component) {
    return !elementsOf(component).filter(ASTBehavior.class).isEmpty();
  }

  public static boolean isApplication(ComponentTypeSymbol component) {
    ASTMTComponentType ast = (ASTMTComponentType) component.getAstNode();
    return ast.getMTComponentModifier().isApplication();
  }

  public static List<PortSymbol> getPortsInGuardExpression(ASTExpression node) {
    List<PortSymbol> ports = new ArrayList<>();

    for (ASTNameExpression guardExpressionElement : getGuardExpressionElements(node)) {
      String name = guardExpressionElement.getName();
      IArcBasisScope s = (IArcBasisScope) node.getEnclosingScope();
      Optional<PortSymbol> port = s.resolvePort(name);
      port.ifPresent(ports::add);
    }
    return ports;
  }

  public static boolean portIsComparedToNoData(ASTExpression e, String portName) {
    List<ASTNameExpression> usedNames = ExpressionUtil.getNameExpressionElements(e);
    for (ASTNameExpression name : usedNames) {
      if (name.getName().equals(portName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true iff the port appears in a batch expression
   */
  public static Boolean isBatchPort(PortSymbol port, ComponentTypeSymbol component) {
    // TODO: Fix for MontiArc 6
    return false;
  }

  /**
   * Returns all ports that appear in any batch statements
   *
   * @return unsorted list of all ports for which a batch statement exists
   */
  public static List<PortSymbol> getPortsInBatchStatement(ComponentTypeSymbol component) {
    List<ASTAnnotatedPort> bufferPorts = elementsOf(component)
        .filter(ASTAnnotatedPort.class)
        .filter(p -> p.getPortAnnotation() instanceof ASTBufferedPort)
        .toList();
    List<String> bufferPortsNames = new ArrayList<>();
    for (ASTAnnotatedPort port : bufferPorts) {
      for (ASTPortDeclaration decl : port.getPortDeclarationList()) {
        for (ASTPort p : decl.getPortList()) {
          bufferPortsNames.add(p.getName());
        }
      }
    }

    List<PortSymbol> ports = new ArrayList<>();
    IArcBasisScope s = component.getSpannedScope();
    for (String bufferedPort : bufferPortsNames) {
      Optional<PortSymbol> resolve = s.resolvePort(bufferedPort);
      resolve.ifPresent(ports::add);
    }
    return ports;
  }

  /**
   * Find all ports of a component that DON'T appear in any batch statement
   */
  public static List<PortSymbol> getPortsNotInBatchStatements(ComponentTypeSymbol component) {
    List<PortSymbol> result = component.getAllPorts();
    result.removeAll(getPortsInBatchStatement(component));
    return result;
  }
}
