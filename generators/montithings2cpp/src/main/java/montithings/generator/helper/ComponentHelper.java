// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._ast.ASTPort;
import montiarc._symboltable.*;
import montiarc._symboltable.adapters.CDTypeSymbol2JavaType;
import montiarc.helper.SymbolPrinter;
import montithings._ast.*;
import montithings._ast.ASTComponent;
import montithings._symboltable.ResourcePortSymbol;
import montithings.generator.codegen.xtend.util.Utils;
import montithings.generator.visitor.NoDataComparisionsVisitor;
import montithings.helper.GenericBindingUtil;
import montithings.visitor.GuardExpressionVisitor;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 *
 * @author Gerrit Leonhardt
 */
public class ComponentHelper {
  private final ComponentSymbol component;

  protected final ASTComponent componentNode;

  public ComponentHelper(ComponentSymbol component) {
    this.component = component;
    if ((component.getAstNode().isPresent()) && (component.getAstNode()
        .get() instanceof ASTComponent)) {
      componentNode = (ASTComponent) component.getAstNode().get();
    }
    else {
      componentNode = null;
    }
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
   * @param componentSymbol Symbol of the component which contains the port
   * @param portSymbol      Symbol of the port for which the type name should be
   *                        determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentSymbol componentSymbol,
      PortSymbol portSymbol) {

    final JTypeReference<? extends JTypeSymbol> typeReference = portSymbol.getTypeReference();
    if (!typeReference.existsReferencedSymbol()) {
      Log.error(
          "0xMA135 Referenced type for port \"" + portSymbol.getName() + "\" does not exist.");
    }

    if (componentSymbol.getPorts().contains(portSymbol) || !componentSymbol.getSuperComponent()
        .isPresent()) {
      // A. Component has no super component or the port is defined in the
      // extending component
      // Therefore, there are no special cases and the name can be used from
      // the typeReference, even if it is a generic type parameter of the
      // defining component
      // return ComponentHelper.autobox(printTypeReference(typeReference));
      // TODO: This is a temporary workaround until MC 5.0.0.1 is used that
      // fixes the JTypeSymbolsHelper
      TypesPrettyPrinterConcreteVisitor typesPrinter = new TypesPrettyPrinterConcreteVisitor(
          new IndentPrinter());
      final ASTPort astNode = (ASTPort) portSymbol.getAstNode().get();
      if (portUsesCdType(portSymbol)) {
        return printCdPortPackageNamespace(componentSymbol, portSymbol);
      }
      return ComponentHelper.autobox(typesPrinter.prettyprint(astNode.getType()));
      // End of temp workaround

    }
    else {
      // TODO Refactor stack and maps to fields of the Helper or remove stack
      // and only save last element
      // B. Component has a super component
      // B.2 Port is inherited from the super component (it is not necessarily
      // defined exactly in the super component)

      // Build the super component hierarchy stack up to the point where
      // the port is defined
      Deque<ComponentSymbolReference> superComponentStack = new ArrayDeque<>();

      // This map contains the string representation of all actual type
      // arguments
      // for each component where types have been pushed through the
      // inheritance hierarchy
      Map<String, Map<String, String>> actualTypeArgStringsMap = new HashMap<>();

      // Fill the stack for the inheritance hierarchy
      // The starting component pushes its super component onto the stack and
      // adds the type arguments to the map without any changes, as type
      // parameters
      // from the initial component do not have to be replaced
      ComponentSymbol currentComponent = componentSymbol;
      Map<String, String> identity = new HashMap<>();
      for (JTypeSymbol typeParam : currentComponent.getFormalTypeParameters()) {
        identity.put(typeParam.getName(), typeParam.getName());
      }
      actualTypeArgStringsMap.put(currentComponent.getFullName(), identity);

      // Note: At this point the super component of the initial component is
      // on the stack and the map for the super component is filled

      while (currentComponent != null && currentComponent.getSuperComponent().isPresent()
          && !currentComponent.getPorts().contains(portSymbol)) {
        Map<String, String> superCompTypeArgs = new HashMap<>();
        final ComponentSymbolReference superCompRef = currentComponent.getSuperComponent().get();
        superComponentStack.push(superCompRef);
        final ComponentSymbol superCompSymbol = superCompRef.getReferencedSymbol();

        // Fill the map for the super component
        for (JTypeSymbol typeSymbol : superCompSymbol.getFormalTypeParameters()) {
          final int index = superCompSymbol.getFormalTypeParameters().indexOf(typeSymbol);
          final ActualTypeArgument actualTypeArg = superCompRef.getActualTypeArguments().get(index);
          String resultingTypeArgument = insertTypeParamValueIntoTypeArg(actualTypeArg.getType(),
              actualTypeArgStringsMap.get(currentComponent.getFullName()));
          superCompTypeArgs.put(typeSymbol.getName(), resultingTypeArgument);
        }
        actualTypeArgStringsMap.put(superCompSymbol.getFullName(), superCompTypeArgs);

        // Prepare the next iteration. Keeps the component at the highest
        // point in the inheritance hierarchy that defines the port
        currentComponent = superComponentStack.peek();
      }

      // Note: At this point the currentComponent is the one that defines the
      // port of which the type is to be determined.
      // The stack contains all super components of the starting component
      // Each component has a map which contains all actual type arguments with
      // the types from the hierarchy replacing type parameters

      // Replace all type parameters of the defining component which occurr in
      // the port type by the actual type argument
      return insertTypeParamValueIntoTypeArg(typeReference,
          actualTypeArgStringsMap.get(Objects.requireNonNull(currentComponent).getFullName()));
    }
  }

  public static boolean portUsesCdType(PortSymbol portSymbol) {
    return portSymbol.getTypeReference().getReferencedSymbol() instanceof CDTypeSymbol2JavaType;
  }

  public static String printCdPortPackageNamespace(ComponentSymbol componentSymbol,
      PortSymbol portSymbol) {
    if (!(portSymbol.getTypeReference().getReferencedSymbol() instanceof CDTypeSymbol2JavaType)) {
      throw new IllegalArgumentException(
          "Can't print namespace of non-CD type " + portSymbol.getTypeReference()
              .getReferencedSymbol().getFullName());
    }
    CDTypeSymbol2JavaType adapter = (CDTypeSymbol2JavaType) portSymbol.getTypeReference()
        .getReferencedSymbol();
    CDTypeSymbol cdTypeSymbol = adapter.getAdaptee();
    return printPackageNamespace(componentSymbol, cdTypeSymbol);
  }

  public static String printPackageNamespace(ComponentSymbol comp, CDTypeSymbol cdtype) {
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

  public static String printPackageNamespaceForComponent(
      montiarc._symboltable.ComponentSymbol comp) {
    List<String> packages = ComponentHelper.getPackages(comp);
    if(comp instanceof montithings._symboltable.ComponentSymbol) {
      if (((montithings._symboltable.ComponentSymbol) comp).isInterfaceComponent()) {
        return "";
      }
    }
    StringBuilder namespace = new StringBuilder("montithings::");
    for (String packageName : packages) {
      namespace.append(packageName).append("::");
    }
    return namespace.toString();
  }

  /**
   * Converts the given type reference {@param toAnalyze} into a String
   * representation where all occurrences of type arguments from the extending or
   * embedding component are replaced by their actual values. The actual values
   * are given as the second argument {@param typeParamMap}. <br/>
   * Example: For a given type argument {@code Map<T,K>} as an
   * {@link TypeReference} object and the {@link Map}
   * {@code ["T" -> "String", "K -> "Integer"]} the resulting String is
   * {@code Map<String, Integer>}.
   *
   * @param toAnalyze    The type argument where the type parameters should be
   *                     replaced.
   * @param typeParamMap The map used to replace the type parameters
   * @return The resulting String representation with replaced type parameters
   */
  public static String insertTypeParamValueIntoTypeArg(
      TypeReference<? extends TypeSymbol> toAnalyze,
      Map<String, String> typeParamMap) {

    StringBuilder result = new StringBuilder();
    result.append(toAnalyze.getName());
    if (toAnalyze.getActualTypeArguments().isEmpty()) {
      // There are no type arguments. Check if the type itself is a type
      // parameter
      if (typeParamMap.containsKey(toAnalyze.getName())) {
        return typeParamMap.get(toAnalyze.getName());
      }
      else {
        return toAnalyze.getName();
      }
    }
    else {
      // There are type arguments and therefore they are processed recursively
      // and reprinted
      result.append("<");
      result.append(toAnalyze.getActualTypeArguments().stream()
          .map(typeArg -> insertTypeParamValueIntoTypeArg(typeArg.getType(), typeParamMap))
          .collect(Collectors.joining(", ")));
      result.append(">");
    }
    for (int i = 0; i < toAnalyze.getDimension(); i++) {
      result.append("[]");
    }
    return result.toString();
  }

  /**
   * Pretty print the ast type node with removed spaces.
   *
   * @param astType The node to print
   * @return The printed node
   */
  public static String printTypeName(ASTType astType) {
    TypesPrettyPrinterConcreteVisitor typesPrinter = new de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor(
        new IndentPrinter());
    return autobox(typesPrinter.prettyprint(astType).replaceAll(" ", ""));
  }

  public static String printCPPTypeName(ASTType astType) {
    return java2cppTypeString(printTypeName(astType));
  }

  /**
   * Prints a type reference with dimension and type arguments.
   *
   * @param reference The type reference to print
   * @return The printed type reference
   */
  public static String printTypeReference(TypeReference<? extends TypeSymbol> reference) {
    return insertTypeParamValueIntoTypeArg(reference, new HashMap<>());
  }

  /**
   * Prints an actual type argument.
   *
   * @param arg The actual type argument to print
   * @return The printed actual type argument
   */
  public static String printTypeArgument(ActualTypeArgument arg) {
    return printTypeReference(arg.getType());
  }

  /**
   * Prints a list of actual type arguments.
   *
   * @param typeArguments The actual type arguments to print
   * @return The printed actual type arguments
   */
  public static String printTypeArguments(List<ActualTypeArgument> typeArguments) {
    if (typeArguments.size() > 0) {
      String result = "<" +
          typeArguments.stream().map(ComponentHelper::printTypeArgument)
              .collect(Collectors.joining(", ")) +
          ">";
      return result;
    }
    return "";
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
    // final List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments =
    // param.getConfigArguments();
    List<ASTExpression> configArguments = param.getConfigArguments();
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());

    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<JFieldSymbol> configParameters = param.getComponentType().getConfigParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponent astNode = (ASTComponent) param.getComponentType().getReferencedSymbol()
          .getAstNode()
          .get();
      final List<ASTParameter> parameters = astNode.getHead().getParameterList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = printer
            .prettyprint(astParameter.getDefaultValue().getExpression());
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }

    return outputParameters;
  }

  /**
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();

    String packageName = Utils
        .printPackageWithoutKeyWordAndSemicolon(
            componentTypeReference.getReferencedComponent().get());
    if (!packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentTypeReference.getName();
    if (componentTypeReference.hasActualTypeArguments()) {
      result += printTypeArguments(componentTypeReference.getActualTypeArguments());
    }
    return result;
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation) {
    return getSubComponentTypeNameWithoutPackage(instance, interfaceToImplementation, true);
  }

  public static String getSubComponentTypeNameWithoutPackage(ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation,
      boolean printTypeParameters) {
    String result = "";
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    result += componentTypeReference.getName();
    if (componentTypeReference.hasActualTypeArguments() && printTypeParameters) {
      // format simple component type name to full component type name
      List<ActualTypeArgument> types = new ArrayList<>(componentTypeReference.getActualTypeArguments());
      types = addTypeParameterComponentPackage(instance,types);
      result += printTypeArguments(types);
    }
    if (interfaceToImplementation.containsKey(result)) {
      return interfaceToImplementation.get(result);
    }
    return result;
  }

  /**
   * Formats simple component type name to qualified component type parameter name
   * @param instance component instance that gets qualified type parameter names
   * @param types types of the component instance, where simple component names are converted to qualified names
   * @return for every type parameter that references a component by it's simple name, it is replaced by the fully qualified component name.
   */
  public static List<ActualTypeArgument> addTypeParameterComponentPackage(ComponentInstanceSymbol instance, List<ActualTypeArgument> types) {
    List<ActualTypeArgument> result = new ArrayList<>(types);
    int size = types.size();
    for(int i =0; i<size;i++) {
      if (types.get(i).getType() instanceof JavaTypeSymbolReference){
        JavaTypeSymbolReference oldType = (JavaTypeSymbolReference)types.get(i).getType();
        String typeName = oldType.getName();
        MontiArcArtifactScope artifactScope = GenericBindingUtil.getEnclosingMontiArcArtifactScope(instance.getEnclosingScope());
        ComponentSymbol boundComponent = GenericBindingUtil.getComponentFromString(artifactScope, typeName);
        if (boundComponent != null) {
          String nameWithNameSpace =printPackageNamespaceForComponent(boundComponent) + typeName;
          JavaTypeSymbolReference javaTypeSymbolReference = new JavaTypeSymbolReference(nameWithNameSpace , oldType.getEnclosingScope(), oldType.getDimension());
          javaTypeSymbolReference.setActualTypeArguments(ComponentHelper.addTypeParameterComponentPackage(instance,oldType.getActualTypeArguments()));
          result.set(i, new ActualTypeArgument(javaTypeSymbolReference));
        }
      }
    }
    return result;
  }

  /**
   * Set of components used as generic type argument as include string
   * @param comp component that gets the new includes
   * @param instance component instance that assigns component to generic
   * @return Set of components used as generic type argument as include string.
   * Is empty if include is not needed.
   */
  public static Set<String> includeGenericComponent(ComponentSymbol comp, ComponentInstanceSymbol instance){
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    if (componentTypeReference.hasActualTypeArguments()) {
      List<ActualTypeArgument> types = new ArrayList<>(componentTypeReference.getActualTypeArguments());
      return includeGenericComponentIterate(comp,instance,types);
    }
    return new HashSet<>();
  }

  public static Set<String> includeGenericComponentIterate(ComponentSymbol comp, ComponentInstanceSymbol instance, List<ActualTypeArgument> types){
    HashSet<String> result = new HashSet<>();
      for(int i =0; i<types.size();i++) {
        String typeName = types.get(i).getType().getName();
        ComponentSymbol boundComponent = GenericBindingUtil.getComponentFromString(
            GenericBindingUtil.getEnclosingMontiArcArtifactScope(instance.getEnclosingScope()), typeName);
        if (boundComponent!= null) {
          result.add(getPackagePath(comp,boundComponent)+typeName);
          result.addAll(includeGenericComponentIterate(comp, instance, types.get(i).getType().getActualTypeArguments()));
        }
      }
    return result;
  }

  /**
   * Replace subcomponent instance type with generic if it is an interface type.
   * component top<T extends InterfaceComponent>{
   *   component InterfaceComponent xy;
   * }
   * results in
   * component top<T extends InterfaceComponent>{
   *   component T xy;
   * }
   *
   * @param comp component containing the subcomponent instances.
   * @param instance the instance where it's type may be replaced.
   * @param interfaceToImplementation binding which replaces an interface type if no generic is used.
   * @return the subcomponent type name without package.
   */
  public static String getSubComponentTypeNameWithBinding(ComponentSymbol comp, ComponentInstanceSymbol instance,
      HashMap<String, String> interfaceToImplementation) {
    HashMap<String, String> interfaceToImplementationGeneric = new HashMap<>(interfaceToImplementation);
    final ComponentSymbolReference componentTypeReference = instance.getComponentType();
    // check if needed optional values are present and if the instance component type is an interface
    if(componentTypeReference.getReferencedComponent().isPresent()
        &&componentTypeReference.getReferencedComponent().get() instanceof montithings._symboltable.ComponentSymbol){
      montithings._symboltable.ComponentSymbol interfaceComp = (montithings._symboltable.ComponentSymbol)componentTypeReference.getReferencedComponent().get();
      if(interfaceComp.isInterfaceComponent()){
        //get interface component type name and the replacing generic name.
        String interfaceCompName = interfaceComp.getName();
        if(comp.getAstNode().isPresent()){
          ASTComponent compBind = (ASTComponent) (comp.getAstNode().get());
          String typeName = GenericBindingUtil.getSubComponentType(compBind, instance);
          // replace the interface component type of the instance with the generic type.
          if(typeName!=null && interfaceCompName!=null) {
            interfaceToImplementationGeneric.remove(interfaceCompName);
            interfaceToImplementationGeneric.put(interfaceCompName, typeName);
          }
        }
      }
    }
    return getSubComponentTypeNameWithoutPackage(instance,interfaceToImplementationGeneric);
  }


  /**
   * Determine whether the port of the given connector is an incoming or outgoing
   * port.
   *
   * @param cmp      The component defining the connector
   * @param isSource Specifies whether the port to check is the source port of the
   *                 connector or the target port
   * @return true, if the port is an incoming port. False, otherwise.
   */
  public boolean isIncomingPort(ComponentSymbol cmp, ASTQualifiedName source,
      ASTQualifiedName target, boolean isSource) {
    String subCompName = getConnectorComponentName(source, target, isSource);
    String portNameUnqualified = getConnectorPortName(source, target, isSource);
    Optional<PortSymbol> port;
    String portName = isSource ? Names.getQualifiedName(source.getPartList())
        : Names.getQualifiedName(target.getPartList());
    // port is of subcomponent
    if (portName.contains(".")) {
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
          .resolve(subCompName, ComponentInstanceSymbol.KIND);
      Optional<ComponentSymbol> subComp = subCompInstance.get().getComponentType()
          .getReferencedComponent();
      port = subComp.get().getSpannedScope().resolve(portNameUnqualified, PortSymbol.KIND);
    }
    else {
      port = cmp.getSpannedScope().resolve(portName, PortSymbol.KIND);
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }

  /**
   * Returns the component name of a connection.
   *
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorComponentName(ASTQualifiedName source, ASTQualifiedName target,
      boolean isSource) {
    final String name;
    if (isSource) {
      name = Names.getQualifiedName(source.getPartList());
    }
    else {
      name = Names.getQualifiedName(target.getPartList());
    }
    if (name.contains(".")) {
      return name.split("\\.")[0];
    }
    return "this";

  }

  /**
   * Returns the port name of a connection.
   *
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorPortName(ASTQualifiedName source, ASTQualifiedName target,
      boolean isSource) {
    final String name;
    if (isSource) {
      name = Names.getQualifiedName(source.getPartList());
    }
    else {
      name = Names.getQualifiedName(target.getPartList());
    }

    if (name.contains(".")) {
      return name.split("\\.")[1];
    }
    return name;
  }

  /**
   * Checks whether the given typeName for the component comp is a generic
   * parameter.
   *
   * @param comp
   * @param typeName
   * @return
   */
  private boolean isGenericTypeName(ASTComponent comp, String typeName) {
    if (comp == null) {
      return false;
    }
    if (comp.getHead().isPresentGenericTypeParameters()) {
      List<ASTTypeVariableDeclaration> parameterList = comp.getHead().getGenericTypeParameters()
          .getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration type : parameterList) {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Prints the type of the reference including dimensions.
   *
   * @param ref
   * @return
   */
  public String printParamTypeName(ASTComponent comp, JTypeReference<? extends JTypeSymbol> ref) {
    StringBuilder name = new StringBuilder(ref.getName());
    for (int i = 0; i < ref.getDimension(); ++i) {
      name.append("[]");
    }
    return java2cppTypeString(name.toString());
  }

  public String printFqnTypeName(ASTComponent comp, JTypeReference<? extends JTypeSymbol> ref) {
    StringBuilder name = new StringBuilder(ref.getName());
    if (isGenericTypeName(comp, name.toString())) {
      return name.toString();
    }
    Collection<JTypeSymbol> sym = ref.getEnclosingScope()
        .resolveMany(ref.getName(), JTypeSymbol.KIND);
    if (!sym.isEmpty()) {
      name = new StringBuilder(sym.iterator().next().getFullName());
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name.append("[]");
    }
    return autobox(java2cppTypeString(name.toString()));
  }

  /**
   * @return A list of String representations of the actual type arguments
   * assigned to the super component
   */
  public List<String> getSuperCompActualTypeArguments() {
    final List<String> paramList = new ArrayList<>();
    if (component.getSuperComponent().isPresent()) {
      final ComponentSymbolReference componentSymbolReference = component.getSuperComponent().get();
      final List<ActualTypeArgument> actualTypeArgs = componentSymbolReference
          .getActualTypeArguments();
      String componentPrefix = this.component.getFullName() + ".";
      for (ActualTypeArgument actualTypeArg : actualTypeArgs) {
        final String printedTypeArg = SymbolPrinter.printTypeArgument(actualTypeArg);
        if (printedTypeArg.startsWith(componentPrefix)) {
          paramList.add(printedTypeArg.substring(componentPrefix.length()));
        }
        else {
          paramList.add(printedTypeArg);
        }
      }
    }
    return paramList;
  }

  /**
   * @return Corresponding CPP types from input java types
   */
  public static String java2cppTypeString(String type) {
    type = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
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

  public static String getRealPortCppTypeString(ComponentSymbol comp, PortSymbol port) {
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
  public static Boolean existsIPCServerHWCClass(File hwcPath, ComponentSymbol comp,
      String resourcePortName) {
    String fqCompName = comp.getPackageName() + "." + comp.getName();
    File implLocation = Paths.get(hwcPath.toString() + File.separator
        + fqCompName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
        + "-" + StringTransformations.capitalize(resourcePortName) + File.separator
        + StringTransformations.capitalize(resourcePortName) + "Server.cpp").toFile();
    return implLocation.isFile();
  }

  /**
   * Get all CPP imports in the given component
   *
   * @param comp
   * @return List of Strings containing all CPP imports of the component
   */
  public static List<String> getCPPImports(ComponentSymbol comp) {
    List<String> importStrings = new ArrayList<>();
    try {
      ASTMTCompilationUnit node = (ASTMTCompilationUnit) comp.getEnclosingScope().getAstNode()
          .get();
      List<ASTMTImportStatement> imports = node.getMTImportStatementList();
      for (ASTMTImportStatement importStatement : imports) {
        if (importStatement instanceof ASTCPPImportStatementSYSTEM) {
          importStrings.add(String.join(".",
              (((ASTCPPImportStatementSYSTEM) importStatement)
                  .getCppSystemImportList())));
        }
        if (importStatement instanceof ASTCPPImportStatementLOCAL) {
          importStrings.add(((ASTCPPImportStatementLOCAL) importStatement).getCppImport());
        }
      }
    }
    catch (Exception ignored) {
    }
    return importStrings;
  }

  /**
   * Returns type of the resource port utilizing the functions for non-resource ports
   *
   * @param port
   * @return Type of port as string
   */
  public static String getResourcePortType(ResourcePortSymbol port) {
    ASTResourcePort node = (ASTResourcePort) port.getAstNode().get();
    TypesPrettyPrinterConcreteVisitor typesPrinter = new TypesPrettyPrinterConcreteVisitor(
        new IndentPrinter());
    return java2cppTypeString(ComponentHelper.autobox(typesPrinter.prettyprint(node.getType())));
  }

  /**
   * Gets a string that corresponds to the update interval of the component in CPP code
   *
   * @param comp
   * @return CPP duration
   */
  public static String getExecutionIntervalMethod(ComponentSymbol comp) {
    int interval = ((ASTComponent) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTCalculationInterval)
        .findFirst()
        .map(e -> ((ASTCalculationInterval) e).getInterval().getValue())
        .orElse(50);
    String method = "std::chrono::milliseconds(" + interval + ")";
    String intervalUnit = ((ASTComponent) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTCalculationInterval)
        .findFirst()
        .map(e -> ((ASTCalculationInterval) e).getTimeUnit().toString())
        .orElse("MSEC");

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
  
  public static String getExecutionIntervalInMillis(ComponentSymbol comp) {
	    int interval = ((ASTComponent) comp.getAstNode().get())
	        .getBody()
	        .getElementList()
	        .stream()
	        .filter(e -> e instanceof ASTControlBlock)
	        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
	        .filter(e -> e instanceof ASTCalculationInterval)
	        .findFirst()
	        .map(e -> ((ASTCalculationInterval) e).getInterval().getValue())
	        .orElse(50);
	    String intervalUnit = ((ASTComponent) comp.getAstNode().get())
	        .getBody()
	        .getElementList()
	        .stream()
	        .filter(e -> e instanceof ASTControlBlock)
	        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
	        .filter(e -> e instanceof ASTCalculationInterval)
	        .findFirst()
	        .map(e -> ((ASTCalculationInterval) e).getTimeUnit().toString())
	        .orElse("MSEC");

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
   * Returns true if
   *
   * @param comp
   * @return
   */
  public static Boolean usesBatchMode(ComponentSymbol comp) {
    return ((ASTComponent) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .anyMatch(e -> e instanceof ASTBatchStatement);
  }

  public static Boolean hasSyncGroups(ComponentSymbol comp) {
    return getSyncGroups(comp).size() > 0;
  }

  /**
   * Returns all synchronization groups as lists of strings for easier code generation
   *
   * @param comp
   * @return
   */
  public static List<List<String>> getSyncGroups(ComponentSymbol comp) {
    return ((ASTComponent) comp.getAstNode().get())
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTSyncStatement)
        .map(e -> ((ASTSyncStatement) e).getSyncedPortList())
        .collect(Collectors.toList());
  }

  /**
   * Returns ports that don't appear in any synchronization group
   *
   * @param comp
   * @return
   */
  public static List<PortSymbol> getPortsNotInSyncGroup(ComponentSymbol comp) {
    List<String> portsInSyncGroups = getSyncGroups(comp)
        .stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    return comp.getAllIncomingPorts()
        .stream()
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
   * Calculates the path for handwritten codes of IPC servers
   *
   * @param port
   * @param comp
   * @param hwcPath
   * @return
   */
  public static File getIPCHWCPath(ResourcePortSymbol port, ComponentSymbol comp, File hwcPath) {
    String fqCompName = comp.getPackageName() + "." + comp.getName();
    return Paths.get(hwcPath.toString() + File.separator
        + fqCompName.replaceAll("\\.", Matcher.quoteReplacement(File.separator))
        + "-" + StringTransformations.capitalize(port.getName())).toFile();
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

  public static String getPackagePath(ComponentSymbol comp, ComponentInstanceSymbol subComp) {
    return getPackagePath(comp, subComp.getComponentType().getReferencedSymbol());
  }

  public static String getPackagePath(ComponentSymbol comp, ComponentSymbol subComp) {
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

  public static List<String> getPackages(ComponentSymbol component) {
    String packageName = component.getPackageName();
    String[] packages = packageName.split("\\.");
    return Arrays.asList(packages);
  }

  public static boolean portIsComparedToNoData(ASTExpression e, String portName) {
    NoDataComparisionsVisitor visitor = new NoDataComparisionsVisitor();
    e.accept(visitor);
    return visitor.getFoundExpressions().stream()
        .map(ASTNameExpression::getName)
        .anyMatch(n -> n.equals(portName));
  }

  /* ============================================================ */
  /* =================== MontiThings Adapter ==================== */
  /* ============================================================ */

  public static List<ASTAssumption> getAssumptions(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getAssumptions();
  }

  public static List<ASTGuarantee> getGuarantees(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getGuarantees();
  }

  public static Boolean hasExecutionStatement(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).hasExecutionStatement();
  }

  /**
   * Get list of execution statements sorted by priority
   *
   * @return list of execution statements sorted by priority
   */
  public static List<ASTExecutionIfStatement> getExecutionStatements(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getExecutionStatements();
  }

  /**
   * Returns a list of ResourcePortSymbols for resources in the component
   *
   * @return ResourcePortSymmbols in component
   */
  public static List<ResourcePortSymbol> getResourcePortsInComponent(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getResourcePortsInComponent();
  }

  /**
   * Returns true iff the port appears in a batch expression
   *
   * @param port
   * @return
   */
  public static Boolean isBatchPort(PortSymbol port, ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).isBatchPort(port);
  }

  /**
   * Returns all ports that appear in any batch statements
   *
   * @return unsorted list of all ports for which a batch statement exists
   */
  public static List<PortSymbol> getPortsInBatchStatement(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getPortsInBatchStatement();
  }

  /**
   * Find all ports of a component that DON'T appear in any batch statement
   *
   * @return
   */
  public static List<PortSymbol> getPortsNotInBatchStatements(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getPortsNotInBatchStatements();
  }

  /**
   * Get Else Statement if one exists
   *
   * @return the (only) else statement within this component's body, Optional.empty if none exists
   */
  public static Optional<ASTExecutionElseStatement> getElseStatement(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).getElseStatement();
  }

  /**
   * Checks if this component uses an automaton
   *
   * @return true iff this component's body contains an automaton
   */
  public static boolean containsAutomaton(ComponentSymbol component) {
    return ((montithings._symboltable.ComponentSymbol) component).containsAutomaton();
  }
}
