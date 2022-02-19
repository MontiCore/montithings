// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.*;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTEveryBlock;
import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._symboltable.DepLanguageSymbol;
import cdlangextension._symboltable.ICDLangExtensionScope;
import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.base.Preconditions;
import conditionbasis._ast.ASTCondition;
import conditioncatch._ast.ASTConditionCatch;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTArcSync;
import montiarc._ast.ASTArcTiming;
import montithings._ast.*;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.ConfigParams.SplittingMode;
import montithings.generator.codegen.util.Utils;
import montithings.generator.prettyprinter.CppPrettyPrinter;
import montithings.generator.visitor.FindAgoQualificationsVisitor;
import montithings.generator.visitor.FindPublishedPortsVisitor;
import montithings.generator.visitor.GuardExpressionVisitor;
import montithings.generator.visitor.NoDataComparisionsVisitor;
import montithings.util.ClassDiagramUtil;
import montithings.util.GenericBindingUtil;
import mtconfig._ast.ASTCompConfig;
import mtconfig._ast.ASTMTCFGTag;
import mtconfig._ast.ASTRequirementStatement;
import mtconfig._ast.ASTSeparationHint;
import mtconfig._symboltable.CompConfigSymbol;
import mtconfig._symboltable.IMTConfigGlobalScope;
import mtconfig._symboltable.IMTConfigScope;
import org.apache.commons.lang3.tuple.Pair;
import portextensions._ast.ASTAnnotatedPort;
import portextensions._ast.ASTBufferedPort;
import portextensions._ast.ASTSyncStatement;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montithings.generator.helper.TypesHelper.java2cppTypeString;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 */
public class ComponentHelper {

  public static Stream<ASTArcElement> elementsOf(
    ComponentTypeSymbol component) {
    return component.getAstNode().getBody().getArcElementList().stream();
  }

  //============================================================================
  // region Imports and Packages
  //============================================================================

  /**
   * Get all imports of a component
   *
   * @param symbol symbol of the component whose imports shall be returned
   * @return list of all import statements of the given component
   */
  public static List<ImportStatement> getImports(ComponentTypeSymbol symbol) {
    while (symbol.getOuterComponent().isPresent()) {
      symbol = symbol.getOuterComponent().get();
    }
    ASTComponentType ast = symbol.getAstNode();
    return ((MontiThingsArtifactScope) ast.getEnclosingScope()).getImportsList();
  }

  public static List<ASTCDEImportStatement> getImportStatements(String name, ConfigParams config) {
    ICDLangExtensionScope cdLangScope = config.getCdLangExtensionScope();
    List<DepLanguageSymbol> depLanguageSymbols = cdLangScope.resolveDepLanguageMany(name + ".Cpp");
    List<ASTCDEImportStatement> importStatements = new ArrayList<>();
    for (DepLanguageSymbol depLanguageSymbol : depLanguageSymbols) {
      importStatements.addAll(depLanguageSymbol.getAstNode().getCDEImportStatementList());
    }
    return importStatements;
  }

  /**
   * Get import for subpackages
   */
  public static String getSubPackageImports(File[] subPackagesPath) {
    StringBuilder packageNames = new StringBuilder();
    String start = "\"./";
    String endCpp = "/*.cpp\"\n";
    String endH = "/*.h\"\n";

    for (File subPackage : subPackagesPath) {
      /*
        Example of build String with 2 subpackages:

        \"./packageName1/*.cpp\"\n
        \"./packageName1/*.h\"\n
        \"./packageName2/*.cpp\"\n
        \"./packageName2/*.h\"\n
       */
      packageNames.append(start).append(subPackage.getName()).append(endCpp);
      packageNames.append(start).append(subPackage.getName()).append(endH);
    }
    return packageNames.toString();
  }

  public static String getSubPackageIncludes(File[] subPackagesPath) {
    StringBuilder packageNames = new StringBuilder();
    String start = "include_directories(./";
    String end = ")\n";

    for (File subPackage : subPackagesPath) {
      /*
        Example of build String with 2 subpackages:

        include_directories(./packageName1)\n
        include_directories(./packageName2)\n
       */
      packageNames.append(start).append(subPackage.getName()).append(end);
    }
    return packageNames.toString();
  }

  public static String getPackagePath(ComponentTypeSymbol comp, ComponentInstanceSymbol subComp) {
    return getPackagePath(comp, subComp.getType());
  }

  public static String getPackagePath(ComponentTypeSymbol comp, ComponentTypeSymbol subComp) {
    // Get package name of subcomponent
    String subCompPackageName = subComp.getPackageName();
    // Check if subcomponent is in different package than parent component
    if (!subCompPackageName.equals(comp.getPackageName())) {
      // Split packageName
      String[] path = subCompPackageName.split("\\.");
      // Build correct package path
      StringBuilder correctPath = new StringBuilder();
      boolean leaveFirstOut = true;
      for (String dir : path) {
        if (leaveFirstOut) {
          leaveFirstOut = false;
          continue;
        }
        correctPath.append(dir).append("/");
      }
      // Return correct path
      return correctPath.toString();
    }
    // If subcomponent is in the same package as component, then no package path before class import required
    return "";
  }

  public static List<String> getPackages(ComponentTypeSymbol component) {
    String packageName = component.getPackageName();
    String[] packages = packageName.split("\\.");
    return Arrays.asList(packages);
  }

  public static String printPackageNamespaceForComponent(ComponentTypeSymbol comp) {
    List<String> packages = ComponentHelper.getPackages(comp);
    packages = packages.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
    if (ComponentHelper.isInterfaceComponent(comp)) {
      return "";
    }
    StringBuilder namespace = new StringBuilder("montithings::");
    for (String packageName : packages) {
      namespace.append(packageName).append("::");
    }
    return namespace.toString();
  }

  // endregion
  //============================================================================
  // region Parameters, Variables and Settings
  //============================================================================

  /**
   * Set of components used as generic type argument as include string
   *
   * @param comp     component that gets the new includes
   * @param instance component instance that assigns component to generic
   * @return Set of components used as generic type argument as include string.
   * Is empty if include is not needed.
   */
  public static Set<String> includeGenericComponent(ComponentTypeSymbol comp, ComponentInstanceSymbol instance) {
    ASTComponentInstantiation instantiation = getInstantiation(instance);
    if (instantiation.getMCType() instanceof ASTMCBasicGenericType) {
      List<ASTMCTypeArgument> types = new ArrayList<>(
        ((ASTMCBasicGenericType) instantiation.getMCType()).getMCTypeArgumentList());
      return includeGenericComponentIterate(comp, instance, types);
    }
    return new HashSet<>();
  }

  public static Set<String> includeGenericComponentIterate(ComponentTypeSymbol comp,
    ComponentInstanceSymbol instance, List<ASTMCTypeArgument> types) {
    HashSet<String> result = new HashSet<>();
    for (ASTMCTypeArgument type : types) {
      String typeName = TypesPrinter.printTypeArgument(type);
      ComponentTypeSymbol boundComponent = GenericBindingUtil.getComponentFromString(
        GenericBindingUtil
          .getEnclosingMontiArcArtifactScope((MontiThingsArtifactScope) comp.getEnclosingScope()),
        typeName);
      if (boundComponent != null) {
        result.add(getPackagePath(comp, boundComponent) + typeName);
        if (type instanceof ASTMCBasicGenericType) {
          result.addAll(includeGenericComponentIterate(comp, instance,
            ((ASTMCBasicGenericType) type).getMCTypeArgumentList()));
        }
      }
    }
    return result;
  }

  /**
   * Returns True iff the given component is an interface component
   */
  public static boolean isInterfaceComponent(ComponentTypeSymbol comp) {
    if (comp.getAstNode() instanceof ASTMTComponentType) {
      ASTMTComponentType astmtComponentType = (ASTMTComponentType) comp.getAstNode();
      return astmtComponentType.getMTComponentModifier().isInterface();
    }
    return false;
  }

  /**
   * Get the names of all types generated for the interfaces implemented by the given component
   */
  public static Set<String> getInterfaceClassNames(ComponentTypeSymbol component, boolean addPrefix) {
    Set<String> namesOfImplementedInterfaces = new HashSet<>();
    for (ComponentTypeSymbol interf : getImplementedComponents(component)) {
      namesOfImplementedInterfaces.add((addPrefix ? ClassDiagramUtil.COMPONENT_TYPE_PREFIX : "") + interf.getName());
    }
    return namesOfImplementedInterfaces;
  }

  public static Set<String> getInterfaceClassNames(ComponentTypeSymbol component) {
    return getInterfaceClassNames(component, true);
  }

  /**
   * Get all types of (interface) components implemented by the given component
   */
  public static Set<ComponentTypeSymbol> getImplementedComponents(ComponentTypeSymbol component) {
    Set<ComponentTypeSymbol> implementsComps = new HashSet<>();
    ASTMTComponentType astmtComponentType = ((ASTMTComponentType) component.getAstNode());

    if (!astmtComponentType.isPresentMTImplements()) {
      return implementsComps;
    }

    for (Optional<ComponentTypeSymbol> compSymbol : astmtComponentType.getMTImplements().getNamesSymbolList()) {
      compSymbol.ifPresent(implementsComps::add);
    }

    return implementsComps;
  }

  /**
   * Get the ports of the (interface) component that has the given name and that is implemented
   * by the given component
   */
  public static Set<PortSymbol> getPortsOfInterface(String interfName, ComponentTypeSymbol comp) {

    Set<ComponentTypeSymbol> allInterfaces = getImplementedComponents(comp);
    if (allInterfaces.isEmpty()) {
      Log.error(String.format("0xMT0800 Requested ports of interface '%s' for component '%s' "
        + "that does not implement any interfaces", interfName, comp.getFullName()));
    }

    Optional<ComponentTypeSymbol> requestedInterface = Optional.empty();
    for (ComponentTypeSymbol interf : allInterfaces) {
      if (interf.getName().equals(interfName)) {
        requestedInterface = Optional.of(interf);
      }
    }

    if (!requestedInterface.isPresent()) {
      Log.error(String.format("0xMT0801 Requested ports of interface '%s' for component '%s' "
        + "that does not implement the given interfaces", interfName, comp.getFullName()));
      System.exit(-1); // unreachable, but silences static analyzer
    }

    return new HashSet<>(requestedInterface.get().getPorts());
  }

  @Deprecated
  public static boolean hasUpdateInterval(ComponentTypeSymbol comp) {
    return elementsOf(comp).filter(ASTCalculationInterval.class::isInstance)
      .map(ASTCalculationInterval.class::cast).findAny().isPresent();
  }

  /**
   * Gets a string that corresponds to the update interval of the component in CPP code
   */
  @Deprecated
  public static String getExecutionIntervalMethod(ComponentTypeSymbol comp) {
    ASTCalculationInterval interval = elementsOf(comp).filter(
        ASTCalculationInterval.class::isInstance).map(ASTCalculationInterval.class::cast).findFirst()
      .orElse(null);
    String method = "std::chrono::";
    method += TypesPrinter.printTime(interval);
    return method;
  }

  @Deprecated
  public static String getExecutionIntervalMethod(ComponentTypeSymbol comp, ASTEveryBlock everyBlock) {
    String method = "std::chrono::";
    method += TypesPrinter.printTime(everyBlock.getSIUnitLiteral());
    return method;
  }

  @Deprecated
  public static String getExecutionIntervalInMillis(ComponentTypeSymbol comp) {
    ASTCalculationInterval interval = elementsOf(comp).filter(
        ASTCalculationInterval.class::isInstance).map(ASTCalculationInterval.class::cast).findFirst()
      .orElse(null);
    if (interval == null) {
      return "50";
    }

    ASTSIUnitLiteral lit = interval.getInterval();
    SIUnitLiteralDecoder decoder = new SIUnitLiteralDecoder();
    double value = decoder.getValue(lit);

    switch (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit())) {
      case ("ns"):
        return "" + (int) (value / (1000 * 1000));
      case ("μs"):
        return "" + (int) (value / 1000);
      case ("ms"):
        return "" + (int) (value);
      case ("s"):
        return "" + (int) (value * 1000);
      case ("min"):
        return "" + (int) (value * 1000 * 60);
    }
    return "50";
  }

  public static List<VariableSymbol> getVariablesAndParameters(ComponentTypeSymbol comp) {
    List<VariableSymbol> fields = ComponentHelper.getFields(comp);
    List<VariableSymbol> params = comp.getParameters();
    fields.removeAll(params);
    List<VariableSymbol> vars = new ArrayList<>(params);
    vars.addAll(fields);
    return vars;
  }

  /**
   * Workaround for the fact that MontiArc returns parameters twice
   */
  public static List<VariableSymbol> getFields(ComponentTypeSymbol component) {
    return new ArrayList<>(component.getFields());
  }

  public static List<VariableSymbol> getArcFieldVariables(ComponentTypeSymbol component) {
    List<VariableSymbol> arcFieldSymbols = new ArrayList<>();
    for (VariableSymbol symbol : getFields(component)) {
      if (symbol.isPresentAstNode() && symbol.getAstNode() instanceof ASTArcField) {
        arcFieldSymbols.add(symbol);
      }
    }
    return arcFieldSymbols;
  }

  public static boolean isArcField(VariableSymbol symbol) {
    return symbol.isPresentAstNode() && symbol.getAstNode() instanceof ASTArcField;
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
  public static List<String> getParamValues(ComponentInstanceSymbol param) {
    List<ASTExpression> configArguments = param.getArguments();
    MontiThingsFullPrettyPrinter printer = CppPrettyPrinter.getPrinter();

    List<String> outputParameters = new ArrayList<>();
    for (int i = 0; i < configArguments.size(); i++) {
      SymTypeExpression requiredType = param.getType().getAstNode().getHead().getArcParameter(i)
        .getSymbol().getType();
      String prettyprint;
      if (requiredType instanceof SymTypeOfNumericWithSIUnit) {
        prettyprint = Utils.printSIExpression(configArguments.get(i), requiredType);
      }
      else {
        prettyprint = printer.prettyprint(configArguments.get(i));
      }
      outputParameters.add(java2cppTypeString(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<VariableSymbol> configParameters = param.getType().getParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponentType astNode = param.getType().getAstNode();
      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTArcParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        String prettyprint;
        if (astParameter.getSymbol().getType() instanceof SymTypeOfNumericWithSIUnit) {
          prettyprint = Utils
            .printSIExpression(astParameter.getDefault(), astParameter.getSymbol().getType());
        }
        else {
          prettyprint = printer.prettyprint(astParameter.getDefault());
        }
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }

    return outputParameters;
  }

  public static List<ASTArcTiming> getTiming(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTArcTiming.class::isInstance)
      .map(ASTArcTiming.class::cast).collect(Collectors.toList());
  }

  public static boolean isTimesync(ComponentTypeSymbol component) {
    return getTiming(component).stream()
      .filter(e -> e.getArcTimeMode() instanceof ASTArcSync)
      .collect(Collectors.toSet()).size() > 0;
  }

  public static boolean isApplication(ComponentTypeSymbol component, ConfigParams config) {
    return component.getFullName().equals(config.getMainComponent());
  }

  public static boolean retainState(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTMTRetainState.class::isInstance)
      .map(ASTMTRetainState.class::cast).findAny().isPresent();
  }

  // endregion
  //============================================================================
  // region Subcomponents
  //============================================================================

  /**
   * Recursively searches for the types of all subcomponents (and subcomponents
   * of subcomponents and so on)
   */
  public static Set<ComponentTypeSymbol> getSubcompTypesRecursive(ComponentTypeSymbol comp) {
    Set<ComponentTypeSymbol> result;
    result = comp.getSubComponents().stream()
      .map(ComponentInstanceSymbol::getType)
      .collect(Collectors.toSet());
    for (ComponentTypeSymbol subcomp : new HashSet<>(result)) {
      result.addAll(getSubcompTypesRecursive(subcomp));
    }
    return result;
  }

  /**
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    final ComponentTypeSymbol componentType = instance.getType();

    String packageName = Utils.printPackageWithoutKeyWordAndSemicolon(
      componentType);
    if (!packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentType.getName();
    if (Utils.hasTypeParameter(componentType)) {
      result += TypesPrinter.printASTTypeArguments(
        Utils.getTypeParameters(componentType));
    }
    return result;
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
    ConfigParams config) {
    return getSubComponentTypeNameWithoutPackage(instance, config, true);
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
    ConfigParams config, boolean printTypeParameters) {
    String result = "";
    final ComponentTypeSymbol componentType = instance.getType();
    //Use the bound name if present.
    Optional<ComponentTypeSymbol> implementation = config.getBinding(instance);
    if (implementation.isPresent()) {
      result += implementation.get().getName();
    }
    else {
      implementation = config.getBinding(componentType);
      if (implementation.isPresent()) {
        result += implementation.get().getName();
      }
      else {
        result += componentType.getName();
      }
    }

    if (Utils.hasTypeParameter(componentType) && printTypeParameters) {
      // format simple component type name to full component type name
      ASTComponentInstantiation instantiation = getInstantiation(instance);
      if (instantiation.getMCType() instanceof ASTMCBasicGenericType) {
        List<ASTMCTypeArgument> types = new ArrayList<>(
          ((ASTMCBasicGenericType) instantiation.getMCType()).getMCTypeArgumentList());
        //TODO: we still need the following call
        //types = addTypeParameterComponentPackage(instance, types);
        result += TypesPrinter.printActualTypeArguments(types);
      }
    }
    return result;
  }

  // endregion
  //============================================================================
  // region Ports
  //============================================================================


  public static Boolean hasSyncGroups(ComponentTypeSymbol comp) {
    return getSyncGroups(comp).size() > 0;
  }

  /**
   * Returns all synchronization groups as lists of strings for easier code generation
   */
  public static List<List<String>> getSyncGroups(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTSyncStatement.class::isInstance)
      .map(ASTSyncStatement.class::cast)
      .map(ASTSyncStatement::getSyncedPortList)
      .collect(Collectors.toList());
  }

  /**
   * Returns ports that don't appear in any synchronization group
   */
  public static List<PortSymbol> getPortsNotInSyncGroup(ComponentTypeSymbol comp) {
    List<String> portsInSyncGroups = getSyncGroups(comp).stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
    return comp.getAllIncomingPorts().stream()
      .filter(p -> !portsInSyncGroups.contains(p.getName()))
      .collect(Collectors.toList());
  }

  public static Optional<PortSymbol> getPortSymbolFromPortAccess(ASTPortAccess portAccess) {
    if (!portAccess.isPresentComponent()) {
      return Optional.of(portAccess.getPortSymbol());
    }
    return portAccess.getComponentSymbol().getType().getPort(portAccess.getPort());
  }

  /**
   * Determine whether the port of the given connector is an incoming or outgoing
   * port.
   *
   * @param cmp        The component defining the connector
   * @param portAccess the port access to evaluate
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
      if (!subCompInstance.isPresent()) {
        Log.error("0xMT802 Could not find component instance for port access");
        System.exit(-1); // unreachable, but silences static analyzer
      }
      ComponentTypeSymbol subComp = subCompInstance.get().getType();
      port = subComp.getSpannedScope().resolvePort(portNameUnqualified);
    }
    else {
      port = cmp.getSpannedScope().resolvePort(portNameUnqualified);
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }

  /**
   * Returns true iff comp contains at least one buffered port
   */
  public static Boolean usesBatchMode(ComponentTypeSymbol comp) {
    return elementsOf(comp).filter(ASTAnnotatedPort.class::isInstance)
      .map(ASTAnnotatedPort.class::cast)
      .anyMatch(e -> e.getPortAnnotation() instanceof ASTBufferedPort);
  }

  /**
   * Returns all ports that appear in any batch statements
   *
   * @return unsorted list of all ports for which a batch statement exists
   */
  public static List<PortSymbol> getPortsInBatchStatement(
    ComponentTypeSymbol component) {
    List<ASTAnnotatedPort> bufferPorts = elementsOf(component).filter(
        ASTAnnotatedPort.class::isInstance).map(ASTAnnotatedPort.class::cast)
      .filter(p -> p.getPortAnnotation() instanceof ASTBufferedPort).collect(Collectors.toList());
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
  public static List<PortSymbol> getPortsNotInBatchStatements(
    ComponentTypeSymbol component) {
    List<PortSymbol> result = component.getAllPorts();
    result.removeAll(getPortsInBatchStatement(component));
    return result;
  }

  /**
   * Returns true iff the port appears in a batch expression
   */
  public static Boolean isBatchPort(PortSymbol port, ComponentTypeSymbol component) {
    return getPortsInBatchStatement(component).contains(port);
  }

  public static boolean portIsComparedToNoData(ASTExpression e, String portName) {
    NoDataComparisionsVisitor visitor = new NoDataComparisionsVisitor();
    e.accept(visitor.createTraverser());
    return visitor.getFoundExpressions().stream()
      .map(ASTNameExpression::getName)
      .anyMatch(n -> n.equals(portName));
  }

  public static Set<PortSymbol> getPublishedPorts(ComponentTypeSymbol component,
    ASTMCJavaBlock statements) {
    FindPublishedPortsVisitor visitor = new FindPublishedPortsVisitor();
    statements.accept(visitor.createTraverser());
    return visitor.getPublishedPorts();
  }

  // endregion
  //============================================================================
  // region Behavior
  //============================================================================

  public static ASTMCJavaBlock getBehavior(ComponentTypeSymbol component) {
    List<ASTBehavior> behaviors = elementsOf(component).filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast).filter(ASTBehavior::isEmptyNames).collect(Collectors.toList());
    Preconditions.checkArgument(!behaviors.isEmpty(),
      "0xMT800 Trying to print behavior of component \"" + component.getName()
        + "\" that has no behavior.");
    Preconditions.checkArgument(behaviors.size() == 1,
      "0xMT801 Trying to print behavior of component \"" + component.getName()
        + "\" which has multiple conflicting behaviors.");
    return behaviors.get(0).getMCJavaBlock();
  }

  public static List<ASTEveryBlock> getEveryBlocks(ComponentTypeSymbol comp) {
    List<ASTMTEveryBlock> mtEveryBlockList = elementsOf(comp).filter(
        ASTMTEveryBlock.class::isInstance).map(ASTMTEveryBlock.class::cast)
      .collect(Collectors.toList());
    List<ASTEveryBlock> everyBlockList = new ArrayList<>();
    for (ASTMTEveryBlock b : mtEveryBlockList) {
      everyBlockList.add(b.getEveryBlock());
    }
    return everyBlockList;
  }

  public static String getEveryBlockName(ComponentTypeSymbol comp, ASTEveryBlock ast) {
    List<ASTEveryBlock> everyBlockList = getEveryBlocks(comp);
    int i = 1;
    for (ASTEveryBlock everyBlock : everyBlockList) {
      if (everyBlock.equals(ast)) {
        if (everyBlock.isPresentName()) {
          return everyBlock.getName();
        }
        else {
          return "__Every" + i;
        }
      }
      i++;
    }

    //this code should normally not be executed
    return "";
  }

  public static boolean isEveryBlock(String name, ComponentTypeSymbol comp) {
    for (ASTEveryBlock everyBlock : getEveryBlocks(comp)) {
      if (getEveryBlockName(comp, everyBlock).equals(name)) {
        return true;
      }
    }
    return false;
  }

  public static List<ASTBehavior> getPortSpecificBehaviors(ComponentTypeSymbol comp) {
    return elementsOf(comp).filter(ASTBehavior.class::isInstance).map(ASTBehavior.class::cast)
      .filter(e -> !e.isEmptyNames()).collect(Collectors.toList());
  }

  public static String getPortSpecificBehaviorName(ComponentTypeSymbol comp, ASTMTBehavior ast) {
    StringBuilder name = new StringBuilder();
    for (String s : ast.getNameList()) {
      name.append("__");
      name.append(StringTransformations.capitalize(s));
    }
    return name.toString();
  }

  public static boolean hasPortSpecificBehavior(ComponentTypeSymbol comp) {
    return !getPortSpecificBehaviors(comp).isEmpty();
  }

  public static boolean usesPort(ASTMTBehavior behavior, PortSymbol port) {
    if (behavior.isEmptyNames()) {
      //standard behavior consumes all ports
      return true;
    }
    return behavior.getNamesSymbolList().contains(Optional.of(port));
  }

  public static boolean hasGeneralBehavior(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(ASTBehavior::isEmptyNames);
  }

  public static ASTBehavior getGeneralBehavior(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .filter(ASTBehavior::isEmptyNames)
      .collect(Collectors.toList()).get(0);
  }

  public static boolean hasBehavior(ComponentTypeSymbol component) {
    return elementsOf(component)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(ASTBehavior::isEmptyNames);
  }

  public static boolean hasStatechart(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTArcStatechart.class::isInstance)
      .map(ASTArcStatechart.class::cast).findAny().isPresent();
  }

  public static Set<PortSymbol> getPublishedPortsForBehavior(ComponentTypeSymbol component) {
    return getPublishedPorts(component, getBehavior(component));
  }

  public static String printJavaBlock(ASTMCJavaBlock block, boolean isLogTracingEnabled) {
    return printJavaBlock(block, isLogTracingEnabled, false);
  }

  public static String printJavaBlock(ASTMCJavaBlock block, boolean isLogTracingEnabled, boolean suppressPostconditions) {
    MontiThingsFullPrettyPrinter printer = CppPrettyPrinter.getPrinter(isLogTracingEnabled, suppressPostconditions);
    return printer.prettyprint(block);
  }

  public static String printBlock(ASTMCBlockStatement ast) {
    return CppPrettyPrinter.getPrinter().prettyprint(ast);
  }

  public static String printExpression(ASTExpression ast) {
    return CppPrettyPrinter.getPrinter().prettyprint(ast);
  }

  public static String printStatementBehavior(ComponentTypeSymbol component, boolean isLogTracingEnabled) {
    return printJavaBlock(ComponentHelper.getBehavior(component), isLogTracingEnabled);
  }

  public static List<ASTInitBehavior> getPortSpecificInitBehaviors(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTInitBehavior.class::isInstance)
      .map(ASTInitBehavior.class::cast)
      .filter(e -> !e.isEmptyNames())
      .collect(Collectors.toList());
  }

  public static String getPortSpecificInitBehaviorName(ComponentTypeSymbol comp, ASTInitBehavior ast) {
    StringBuilder name = new StringBuilder();
    for (String s : ast.getNameList()) {
      name.append("__");
      name.append(StringTransformations.capitalize(s));
    }
    return name.toString();
  }

  public static Set<PortSymbol> getPublishedPortsForInitBehavior(ComponentTypeSymbol component) {
    return getPublishedPorts(component, getInitBehavior(component));
  }

  public static ASTMCJavaBlock getInitBehavior(ComponentTypeSymbol component) {
    List<ASTInitBehavior> initBehaviors = elementsOf(component)
      .filter(ASTInitBehavior.class::isInstance)
      .map(ASTInitBehavior.class::cast)
      .filter(ASTInitBehavior::isEmptyNames)
      .collect(Collectors.toList());
    Preconditions.checkArgument(!initBehaviors.isEmpty(),
            "0xMT800 Trying to print behavior of component \"" + component.getName()
                    + "\" that has no behavior.");
    Preconditions.checkArgument(initBehaviors.size() == 1,
            "0xMT801 Trying to print behavior of component \"" + component.getName()
                    + "\" which has multiple conflicting behaviors.");
    return initBehaviors.get(0).getMCJavaBlock();
  }

  public static boolean hasInitBehavior(ComponentTypeSymbol component) {
    return elementsOf(component)
      .filter(ASTInitBehavior.class::isInstance)
      .map(ASTInitBehavior.class::cast)
      .anyMatch(ASTInitBehavior::isEmptyNames);
  }

  public static boolean hasInitBehavior(ComponentTypeSymbol component, ASTBehavior behavior) {
    for (ASTInitBehavior initBehavior : getPortSpecificInitBehaviors(component)){
      if (behavior.containsAllNames(initBehavior.getNameList()) && initBehavior.containsAllNames(behavior.getNameList())){
        return true;
      }
    }
    return false;
  }

  public static ASTInitBehavior getInitBehavior(ComponentTypeSymbol component, ASTBehavior behavior) {
    for (ASTInitBehavior initBehavior : getPortSpecificInitBehaviors(component)){
      if (behavior.containsAllNames(initBehavior.getNameList()) && initBehavior.containsAllNames(behavior.getNameList())){
        return initBehavior;
      }
    }
    return null;
  }

  public static String getInitBehaviorName(ComponentTypeSymbol component, ASTBehavior behavior) {
    return getPortSpecificInitBehaviorName(component,
      Objects.requireNonNull(getInitBehavior(component, behavior)));
  }

  public static List<ASTInitBehavior> getInitBehaviorsWithoutBehaviors(ComponentTypeSymbol component) {
    List<ASTBehavior> behaviors = getPortSpecificBehaviors(component);
    List<ASTInitBehavior> initBehaviors = getPortSpecificInitBehaviors(component);
    for (ASTBehavior behavior : behaviors) {
      if (hasInitBehavior(component, behavior)) {
        initBehaviors.remove(getInitBehavior(component, behavior));
      }
    }
    return initBehaviors;
  }

  public static List<ASTMTBehavior> getPortSpecificMTBehaviors(ComponentTypeSymbol component) {
    List<ASTBehavior> behaviors = getPortSpecificBehaviors(component);
    List<ASTInitBehavior> initBehaviors = getInitBehaviorsWithoutBehaviors(component);
    List<ASTMTBehavior> mTBehaviors = new ArrayList<>();
    mTBehaviors.addAll(behaviors);
    mTBehaviors.addAll(initBehaviors);
    return mTBehaviors;
  }

  // endregion
  //============================================================================
  // region Pre / Postconditions
  //============================================================================

  public static List<ASTCondition> getConditions(ComponentTypeSymbol component) {

    // get uncatched conditions
    List<ASTCondition> conditions = elementsOf(component)
      .filter(ASTMTCondition.class::isInstance)
      .map(ASTMTCondition.class::cast)
      .map(ASTMTCondition::getCondition)
      .collect(Collectors.toList());

    // get catched conditions
    List<ASTMTCatch> catchedConditions = elementsOf(component)
      .filter(ASTMTCatch.class::isInstance)
      .map(ASTMTCatch.class::cast)
      .collect(Collectors.toList());
    conditions.addAll(catchedConditions.stream()
      .map(c -> c.getConditionCatch().getCondition())
      .collect(Collectors.toList()));
    return conditions;
  }

  public static Optional<ASTConditionCatch> getCatch(ComponentTypeSymbol component,
    ASTCondition condition) {
    Optional<ASTConditionCatch> result = Optional.empty();
    List<ASTMTCatch> catchedConditions = elementsOf(component).filter(ASTMTCatch.class::isInstance)
      .map(ASTMTCatch.class::cast).collect(Collectors.toList());
    Optional<ASTMTCatch> mtcatch = catchedConditions.stream()
      .filter(c -> c.getConditionCatch().getCondition() == condition)
      .findFirst();
    if (mtcatch.isPresent()) {
      result = Optional.of(mtcatch.get().getConditionCatch());
    }
    return result;
  }

  public static List<ASTPrecondition> getPreconditions(ComponentTypeSymbol component) {
    List<ASTCondition> conditions = getConditions(component);

    return conditions.stream()
      .filter(c -> c instanceof ASTPrecondition)
      .map(c -> (ASTPrecondition) c)
      .collect(Collectors.toList());
  }

  public static List<ASTPostcondition> getPostconditions(ComponentTypeSymbol component) {
    List<ASTCondition> conditions = getConditions(component);

    return conditions.stream()
      .filter(c -> c instanceof ASTPostcondition)
      .map(c -> (ASTPostcondition) c)
      .collect(Collectors.toList());
  }

  public static List<ASTMTCatch> getCatchedConditions(ComponentTypeSymbol component) {
    return elementsOf(component).filter(ASTMTCatch.class::isInstance).map(ASTMTCatch.class::cast)
      .collect(Collectors.toList());
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   */
  public static List<ASTNameExpression> getGuardExpressionElements(ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor.createTraverser());
    return visitor.getExpressions();
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

  // endregion
  //============================================================================
  // region SI Units
  //============================================================================

  public static boolean isSIUnitPort(ASTPortAccess portAccess) {
    Optional<PortSymbol> ps = getPortSymbolFromPortAccess(portAccess);
    return ps.filter(ComponentHelper::isSIUnitPort).isPresent();
  }

  public static boolean isSIUnitPort(PortSymbol portSymbol) {
    return portSymbol.getType() instanceof SymTypeOfNumericWithSIUnit;
  }

  public static ASTComponentInstantiation getInstantiation(ComponentInstanceSymbol instance) {
    ASTNode node = instance.getEnclosingScope().getSpanningSymbol().getAstNode();
    if (!(node instanceof ASTComponentType)) {
      Log.error("0xMT0789 instance is not spanned by ASTComponentType.");
      System.exit(-1); // unreachable, but silences static analyzer
    }
    Optional<ASTComponentInstantiation> result = ((ASTComponentType) node)
      .getSubComponentInstantiations()
      .stream().filter(i -> i.getComponentInstanceList().contains(instance.getAstNode()))
      .findFirst();
    if (!result.isPresent()) {
      Log.error("0xMT0790 instance not found.");
      System.exit(-1); // unreachable, but silences static analyzer
    }
    return result.get();
  }

  public static List<String> getSIUnitPortNames(ComponentTypeSymbol comp) {
    List<String> names = new ArrayList<>();
    for (PortSymbol ps : comp.getAllIncomingPorts()) {
      if (ps.getType() instanceof SymTypeOfNumericWithSIUnit) {
        names.add(ps.getName());
      }
    }
    return names;
  }

  // endregion
  //============================================================================
  // region OCL
  //============================================================================

  public static Map<String, Double> getAgoQualifications(ComponentTypeSymbol comp) {
    FindAgoQualificationsVisitor visitor = new FindAgoQualificationsVisitor();
    if (comp.isPresentAstNode()) {
      comp.getAstNode().accept(visitor.createTraverser());
    }
    return visitor.getAgoQualifications();
  }

  public static boolean hasAgoQualification(ComponentTypeSymbol comp, VariableSymbol variable) {
    return getAgoQualifications(comp).containsKey(variable.getName());
  }

  public static boolean hasAgoQualification(ComponentTypeSymbol comp, PortSymbol port) {
    return getAgoQualifications(comp).containsKey(port.getName());
  }

  public static String getHighestAgoQualification(ComponentTypeSymbol comp, String name) {
    double valueInSeconds = getAgoQualifications(comp).get(name);
    //return as nanoseconds
    return "" + ((long) (valueInSeconds * 1000000000));
  }

  // endregion
  //============================================================================
  // region Splitting
  //============================================================================

  public static List<Pair<ComponentTypeSymbol, String>> getExecutableInstances(
    ComponentTypeSymbol topComponent, ConfigParams config) {
    if (config.getSplittingMode() == SplittingMode.OFF) {
      // If splitting is turned off, splitting hints are ignored.
      return getInstances(topComponent);
    }
    else {
      // If the component includes its subcomponents (recursively), its
      // subcomponents do not need to be executed individually.
      return getInstances(topComponent, topComponent.getFullName(),
        (ComponentTypeSymbol component) -> !shouldIncludeSubcomponents(component, config));
    }
  }

  public static boolean shouldIncludeSubcomponents(ComponentTypeSymbol component,
    ConfigParams config) {
    IMTConfigGlobalScope cscope = config.getMtConfigScope();
    if (cscope != null) {
      Optional<CompConfigSymbol> cfg = cscope
        .resolveCompConfig(config.getTargetPlatform().toString(), component);
      if (cfg.isPresent()) {
        CompConfigSymbol cc = cfg.get();
        ASTCompConfig acc = cc.getAstNode();
        return acc.getMTCFGTagList().stream()
          .filter(ASTSeparationHint.class::isInstance)
          .map(ASTSeparationHint.class::cast)
          .findAny().isPresent();
      }
    }
    return false;
  }

  /**
   * Collects all instances of components recursively. Subcomponents are only
   * considered if {@code recursionPredicate} returns true for the given
   * component.
   *
   * @param recursionPredicate Returns whether to consider the sub components.
   */
  protected static List<Pair<ComponentTypeSymbol, String>> getInstances(
    ComponentTypeSymbol component, String packageName,
    Predicate<ComponentTypeSymbol> recursionPredicate) {
    List<Pair<ComponentTypeSymbol, String>> instances = new ArrayList<>();
    instances.add(Pair.of(component, packageName));

    if (recursionPredicate.test(component)) {
      for (ComponentInstanceSymbol subcomp : component.getSubComponents()) {
        instances.addAll(
          getInstances(subcomp.getType(), packageName + "." + subcomp.getName(),
            recursionPredicate));
      }
    }

    return instances;
  }

  public static List<Pair<ComponentTypeSymbol, String>> getInstances(
    ComponentTypeSymbol topComponent) {
    return getInstances(topComponent, topComponent.getFullName());
  }

  protected static List<Pair<ComponentTypeSymbol, String>> getInstances(
    ComponentTypeSymbol component, String packageName) {
    return getInstances(component, packageName, (ComponentTypeSymbol symbol) -> true);
  }

  // endregion
  //============================================================================
  // region Deployment
  //============================================================================

  /**
   * Collects the requirements from the MTConfig for the specified component and
   * all its subcomponents that are included in the same executable.
   *
   * @return A {@link List} of requirements for the component.
   */
  public static Set<String> getRequirements(ComponentTypeSymbol topComponent, ConfigParams config) {
    if (config.getSplittingMode() == SplittingMode.OFF) {
      // If splitting is turned off, all the components share one executable.
      return getRequirements(topComponent, true, config);
    }
    else {
      // If the component includes its subcomponents (recursively), then this
      // component
      // inherits the requirements of its subcomponents.
      return getRequirements(topComponent, shouldIncludeSubcomponents(topComponent, config),
        config);
    }
  }

  /**
   * Collects the requirements from the MTConfig for the specified component. If
   * {@code recursive == true}, this also includes the requirements of its
   * subcomponents.
   *
   * @return A {@link List} of requirements for the component.
   */
  public static Set<String> getRequirements(ComponentTypeSymbol topComponent, boolean recursive,
    ConfigParams config) {
    HashSet<String> requirements = new HashSet<>();

    IMTConfigGlobalScope cfgScope = config.getMtConfigScope();
    if (cfgScope == null) {
      // Fail fast: there cannot be any requirements without a config scope.
      return requirements;
    }

    Optional<CompConfigSymbol> cfgOpt = cfgScope
      .resolveCompConfig(config.getTargetPlatform().toString(), topComponent);
    if (cfgOpt.isPresent()) {
      CompConfigSymbol cfg = cfgOpt.get();
      ASTCompConfig acc = cfg.getAstNode();

      // Iterate over all MTCFG tags and find the requirement statements.
      for (ASTMTCFGTag tag : acc.getMTCFGTagList()) {
        if (tag instanceof ASTRequirementStatement) {
          ASTRequirementStatement rtag = (ASTRequirementStatement) tag;
          IMTConfigScope rtagScope = rtag.getSpannedScope();
          if (rtagScope != null) {
            // Merge the requirements of this scope into our requirements.
            requirements.addAll(rtag.getSpannedScope().getPropertySymbols().keySet());
          }
        }
      }
    }

    if (recursive) {
      // Merge our requirements with the requirements of our subcomponents.
      for (ComponentInstanceSymbol comp : topComponent.getSubComponents()) {
        requirements.addAll(getRequirements(comp.getType(), true, config));
      }
    }

    return requirements;
  }

  // endregion
  //============================================================================
  // region Other
  //============================================================================

  /**
   * @return Returns true if a handwritten implementation for the IPC Server exists
   */
  @Deprecated
  public static Boolean existsIPCServerHWCClass(File hwcPath, ComponentTypeSymbol comp,
    String resourcePortName) {
    String fqCompName = comp.getPackageName() + "." + comp.getName();
    File implLocation = Paths.get(hwcPath.toString() + File.separator
      + fqCompName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
      + "-" + StringTransformations.capitalize(resourcePortName) + File.separator
      + StringTransformations.capitalize(resourcePortName) + "Server.cpp").toFile();
    return implLocation.isFile();
  }

  public static boolean isFlaggedAsGenerated(ComponentTypeSymbol comp) {
    // the record and replay trafos will include a specific pre comment in all newly generated components
    if (comp.getAstNode().getHead().get_PreCommentList().isEmpty()) {
      return false;
    }

    return comp.getAstNode().getHead().get_PreCommentList().get(0).getText()
      .equals("RECORD_AND_REPLAY_GENERATED");
  }
}
