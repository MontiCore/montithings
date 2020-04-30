// (c) https://github.com/MontiCore/monticore

package montithings._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.*;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._symboltable.MontiArcSymbolTableCreator;
import montiarc.helper.JavaDefaultTypesManager;
import montiarc.helper.Timing;
import montithings._ast.ASTComponent;
import montithings._ast.ASTPort;
import montithings._ast.*;
import montithings._visitor.MontiThingsVisitor;
import montithings.visitor.ExpressionEnclosingScopeSetterVisitor;

import java.util.*;

public class MontiThingsSymbolTableCreator extends MontiArcSymbolTableCreator
    implements MontiThingsVisitor {

  private final static JTypeSymbolsHelper.JTypeReferenceFactory<JavaTypeSymbolReference> javaTypeRefFactory =
      JavaTypeSymbolReference::new;

  public MontiThingsSymbolTableCreator(
      final ResolvingConfiguration resolvingConfig, final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public MontiThingsSymbolTableCreator(final ResolvingConfiguration resolvingConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }

  /**
   * START Adapted from MontiArc
   */
  // inner components.
  protected Stack<ComponentSymbol> componentStack = new Stack<>();

  private ASTComponent currentComponent;

  private final static JavaSymbolFactory javaSymbolFactory = new JavaSymbolFactory();
  /**
   * END Adapted from MontiArc
   */

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(montithings._ast.ASTMontiThingsNode rootNode) {
    Log.errorIfNull(rootNode,
        "0xA7004x317 Error by creating of the MontiThingsSymbolTableCreator symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }

  private MontiThingsVisitor realThis = this;

  public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTMTCompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
        MontiThingsSymbolTableCreator.class.getSimpleName());
    this.compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackageList());
    List<ImportStatement> imports = new ArrayList();
    Iterator var3 = compilationUnit.getImportStatementLOCALList().iterator();

    while (var3.hasNext()) {
      ASTImportStatementLOCAL astImportStatement = (ASTImportStatementLOCAL) var3.next();
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport,
          astImportStatement.isStar());
      imports.add(importStatement);
    }

    JavaDefaultTypesManager.addJavaDefaultImports(imports);
    this.autoinstantiate.push(true);
    ArtifactScope artifactScope = new MontiArcArtifactScope(Optional.empty(),
        this.compilationUnitPackage, imports);
    this.currentImports = imports;
    artifactScope.setAstNode(compilationUnit);
    compilationUnit.setSpannedScope(artifactScope);
    this.putOnStack(artifactScope);
  }

  @Override
  public void endVisit(ASTMACompilationUnit node) {
    this.removeCurrentScope();
  }

  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
    }
  }

  @Override
  public void visit(montithings._ast.ASTResourcePort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    ResourcePortSymbol sym = new ResourcePortSymbol(node.getName());

    int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
    JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
        currentScope().get(), dimension);
    addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get());
    sym.setTypeReference(typeRef);
    sym.setDirection(node.isIncoming());
    String uri = node.getUri();
    sym.setUri(uri);
    sym.setResourceParameters(node.getResourceParameterList());
    if (uri.startsWith("tcp://")) {
      sym.setProtocol("tcp");
    }
    else if (uri.startsWith("ipc://")) {
      sym.setProtocol("ipc");
    }
    else if (uri.startsWith("ws://")) {
      sym.setProtocol("ws");
    }
    else {
      sym.setProtocol("filesystem");
    }

    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
        sym.addStereotype(st.getName(), st.getValue());
      }
    }

    addToScopeAndLinkWithNode(sym, node);
  }

  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef,
      ASTType astType, Scope definingScope) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, definingScope,
        javaTypeRefFactory);
  }

  @Override
  public void visit(montithings._ast.ASTResourceInterface node) {
  }

  @Override
  public void visit(montithings._ast.ASTSyncStatement ast) {
    SyncStatementSymbol syncStatement = create_SyncStatement(ast);
    initialize_SyncStatement(syncStatement, ast);
    addToScopeAndLinkWithNode(syncStatement, ast);
  }

  private SyncStatementSymbol create_SyncStatement(montithings._ast.ASTSyncStatement ast) {
    return new SyncStatementSymbol(ast.getName());
  }

  private void initialize_SyncStatement(SyncStatementSymbol syncStatement,
      montithings._ast.ASTSyncStatement ast) {

  }

  /**
   * The next two methods are necessary so that Guard Expression get an Enclosing Scope
   * in their AST, which is used during generation.
   *
   * @param node
   */
  @Override
  public void visit(ASTExecutionBlock node) {
    node.setEnclosingScope(currentScope().get());
  }

  @Override
  public void endVisit(ASTExecutionBlock node) {
    setEnclosingScopeOfNodes(node);
  }

  /**
   * START Adapted from MontiArc
   */

  @Override
  public void visit(ASTPort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);

    List<ASTMTPortDeclaration> declarations = node.getMTPortDeclarationList();

    if (node.getMTPortDeclarationList().isEmpty()) {
      declarations.add(MontiThingsNodeFactory
          .createASTMTPortDeclaration(StringTransformations.uncapitalize(typeName),
              Optional.empty()));
    }

    for (ASTMTPortDeclaration decl : node.getMTPortDeclarationList()) {
      montithings._symboltable.PortSymbol sym = new montithings._symboltable.PortSymbol(
          decl.getName());

      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);

      addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get());

      sym.setTypeReference(typeRef);
      sym.setDirection(node.isIncoming());

      if (decl.isPresentDefault()) {
        sym.setDefaultValue(decl.getDefault());
        sym.getDefaultValue().setEnclosingScope(currentScope().get());
        setEnclosingScopeOfNodes(sym.getDefaultValue());
      }

      if (node.isPresentAllowedValues()) {
        sym.setAllowedValues(node.getAllowedValues());
      }

      // stereotype
      if (node.getStereotypeOpt().isPresent()) {
        for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
          sym.addStereotype(st.getName(), st.getValue());
        }
      }

      addToScopeAndLinkWithNode(sym, node);
    }
  }

  @Override
  public void visit(ASTComponent node) {
    this.currentComponent = node;
    String componentName = node.getName();

    String componentPackageName = "";
    if (componentStack.isEmpty()) {
      // root component (most outer component of the diagram)
      componentPackageName = compilationUnitPackage;
    }
    else {
      // inner component uses its parents component full name as package
      componentPackageName = componentStack.peek().getFullName();
    }
    ComponentSymbol component = new ComponentSymbol(componentName);
    component.setImports(currentImports);
    component.setPackageName(componentPackageName);

    // generic type parameters
    addTypeParametersToComponent(component, node.getHead().getGenericTypeParametersOpt(),
        component.getSpannedScope());

    // parameters
    setParametersOfComponent(component, node.getHead());

    // stereotype
    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue st : node.getStereotype().getValuesList()) {
        component.addStereotype(st.getName(), Optional.of(st.getValue()));
      }
    }

    // check if this component is an inner component
    if (!componentStack.isEmpty()) {
      component.setDefiningComponent(componentStack.peek());
    }

    // timing
    component.setBehaviorKind(Timing.getBehaviorKind(node));

    // add upperbound type parameters as Java type symbol to component
    Optional<ASTTypeParameters> optionalTypeParameters = node.getHead().getGenericTypeParametersOpt();
    if (optionalTypeParameters.isPresent()) {
      // component has type parameters -> translate AST to Java Symbols and add
      // these to the
      // componentSymbol.
      ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getTypeVariableDeclarationList()) {
        Set<ASTType> types = new HashSet<>(astTypeParameter.getUpperBoundList());
        for (ASTType type:types)
        {
          if(type instanceof ASTComplexReferenceType &&
              !component.getSpannedScope().getLocalSymbols().containsKey(((ASTComplexReferenceType) type).getSimpleReferenceType(0).getName(0))){
          // TypeParameters/TypeVariables are seen as type declarations.
          JavaTypeSymbol javaTypeVariableSymbol = javaSymbolFactory.createTypeVariable(((ASTComplexReferenceType) type).getSimpleReferenceType(0).getName(0));

          // reuse JavaDSL
          JTypeSymbolsHelper.addInterfacesToType(javaTypeVariableSymbol, new ArrayList<>(), component.getSpannedScope(), javaTypeRefFactory);

          component.addFormalTypeParameter(javaTypeVariableSymbol);
          }
        }
      }
    }

    componentStack.push(component);

    addToScopeAndLinkWithNode(component, node);

    // Transform SimpleConncetors to normal qaualified connectors
    for (ASTSubComponent astSubComponent : node.getSubComponents()) {
      for (ASTSubComponentInstance astSubComponentInstance : astSubComponent.getInstancesList()) {
        simpleConnectorTrafo.transform(astSubComponentInstance, component);
      }
    }

    autoinstantiate.push(autoinstantiate.peek());
    autoConnectionTrafo.transformAtStart(node, component);

    // Fix AST enclosing scopes
    List<ASTAssumption> assumptions = component.getAssumptions();
    assumptions.forEach(e -> e.getGuard()
        .accept(new ExpressionEnclosingScopeSetterVisitor(component.getSpannedScope())));
    List<ASTGuarantee> guarantees = component.getGuarantees();
    guarantees.forEach(e -> e.getGuard()
        .accept(new ExpressionEnclosingScopeSetterVisitor(component.getSpannedScope())));
  }

  // Returns mapping between the generic name and the interface component.
  private Map<String,ASTSimpleReferenceType> getGenericBindings(ASTComponent node){
    Map<String,ASTSimpleReferenceType> genericToInterface = new HashMap<>();
    if (node.getHead().getGenericTypeParametersOpt().isPresent()) {
      List<ASTTypeVariableDeclaration> generics = node.getHead().getGenericTypeParameters().getTypeVariableDeclarationList();
      for (int i = 0; i < generics.size(); i++) {
        if(generics.get(i).getUpperBoundList().size()>0){
          ASTSimpleReferenceType interfaceComp = generics.get(i).getUpperBound(0).getSimpleReferenceType(0);
          String typeName = generics.get(i).getName();
          genericToInterface.put(typeName,interfaceComp);
        }
      }
    }
    return genericToInterface;
  }

  protected void setParametersOfComponent(final ComponentSymbol componentSymbol,
      final ASTComponentHead astMethod) {
    for (ASTParameter astParameter : astMethod.getParameterList()) {
      final String paramName = astParameter.getName();
      astParameter.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(astParameter);
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());
      JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
          TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
              .getType()),
          componentSymbol.getSpannedScope(), dimension);

      addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType(),
          componentSymbol.getSpannedScope());
      final JFieldSymbol parameterSymbol = javaSymbolFactory.createFormalParameterSymbol(paramName,
          (JavaTypeSymbolReference) paramTypeSymbol);
      componentSymbol.addConfigParameter(parameterSymbol);
    }
  }

  @Override
  public void endVisit(ASTComponent node) {
    ComponentSymbol component = componentStack.pop();

    // super component
    if (node.getHead().isPresentSuperComponent()) {
      ASTReferenceType superCompRef = node.getHead().getSuperComponent();
      String superCompName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(superCompRef);

      ComponentSymbolReference ref = new ComponentSymbolReference(superCompName,
          currentScope().get());
      ref.setAccessModifier(BasicAccessModifier.PUBLIC);
      // actual type arguments
      addTypeArgumentsToComponent(ref, superCompRef);

      component.setSuperComponent(ref);
    }

    Map<String,ASTSimpleReferenceType> genericToInterface = getGenericBindings(node);
    // changes the subcomponent gneric types to their interface type.
    for (ASTSubComponent subComp : node.getSubComponents()) {
     ASTReferenceType type = subComp.getType();
      if(genericToInterface.containsKey(TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type))){
        subComp.setType(genericToInterface.get(TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type)));
        addSubComponentSymbols(subComp);
        subComp.setType(type);
      }
      else{
        addSubComponentSymbols(subComp);
      }
    }

    if (autoinstantiate.pop()) {
      Collection<ComponentInstanceSymbol> instanceSymbols = component.getSpannedScope()
          .resolveLocally(ComponentInstanceSymbol.KIND);
      Collection<ComponentSymbol> componentSymbols = new ArrayList<>(
          component.getSpannedScope().resolveLocally(ComponentSymbol.KIND));
      componentSymbols.removeIf(c -> instanceSymbols.stream()
          .anyMatch(i -> c.getName().equals(i.getComponentType().getName())));
      componentSymbols.forEach(componentSymbol -> {
        if (componentSymbol.hasConfigParameters() || componentSymbol.hasFormalTypeParameters()) {
          Log.error(String.format(
              "0xMA038 It was not possible to automatically create an instance " +
                  "of component %s because it has generic type or component parameters",
              componentSymbol.getName()));
        }
        else {
          ComponentSymbolReference symbolReference = new ComponentSymbolReference(
              componentSymbol.getName(), currentScope().get());
          String instanceName = StringTransformations.uncapitalize(componentSymbol.getName());
          ComponentInstanceSymbol instanceSymbol = new ComponentInstanceSymbol(instanceName
              , symbolReference);
          addToScope(instanceSymbol);

          ASTSubComponentInstance subComponentInstance = MontiArcMill.subComponentInstanceBuilder()
              .setName(instanceName).setSymbol(instanceSymbol)
              .setEnclosingScope(currentScope().get()).build();
          ASTSimpleReferenceType referenceType = MontiArcMill.simpleReferenceTypeBuilder()
              .addName(componentSymbol.getName()).build();
          ASTSubComponent subComponent = MontiArcMill.subComponentBuilder()
              .addInstances(subComponentInstance).setType(referenceType).build();
          node.getBody().addElement(subComponent);

          instanceSymbol.setAstNode(subComponentInstance);

          Log.debug("Automatically created component instance " + instanceSymbol.getName()
                  + " for inner component " + componentSymbol.getName(),
              MontiArcSymbolTableCreator.class.getSimpleName());
        }
      });
    }

    removeCurrentScope();

    // for inner components the symbol must be fully created to reference it.
    // Hence, in endVisit we
    // can reference it and put the instance of the inner component into its
    // parent scope.

    if (component.isInnerComponent()) {
      String referencedComponentTypeName = component.getFullName();
      ComponentSymbolReference refEntry = new ComponentSymbolReference(
          referencedComponentTypeName, component.getSpannedScope());
      refEntry.setReferencedComponent(Optional.of(component));

      if (node.getInstanceNameOpt().isPresent()) {
        if ((component.hasFormalTypeParameters()
            && !node.getActualTypeArgumentOpt().isPresent())
            || component.hasConfigParameters()) {
          Log.error(String.format(
              "0xMA038 It was not possible to automatically create an instance " +
                  "of component %s because it has type or constructor " +
                  "parameters that were not assigned in the instance.",
              component.getName()));
        }
        else {
          // create instance
          String instanceName = node.getInstanceNameOpt().get();

          if (node.getActualTypeArgumentOpt().isPresent()) {
            setActualTypeArgumentsOfCompRef(refEntry,
                node.getActualTypeArgument().getTypeArgumentList());
          }

          ComponentInstanceSymbol instanceSymbol
              = new ComponentInstanceSymbol(instanceName, refEntry);
          Log.debug("Created component instance " + instanceSymbol.getName()
                  + " referencing component type " + referencedComponentTypeName,
              MontiArcSymbolTableCreator.class.getSimpleName());

          addToScope(instanceSymbol);
        }
      }
      // check whether there are already instances of the inner component type
      // defined in the component type. We then have to set the referenced
      // component.
      Collection<ComponentInstanceSymbol> instances = component.getEnclosingScope()
          .resolveLocally(ComponentInstanceSymbol.KIND);

      for (ComponentInstanceSymbol instance : instances) {
        if (instance.getComponentType().getName().equals(component.getName())) {
          instance.getComponentType().setReferencedComponent(Optional.of(component));
        }
      }
    }
    autoConnectionTrafo.transformAtEnd(node, component);
  }

  private void setActualTypeArgumentsOfCompRef(ComponentSymbolReference typeReference,
      List<ASTTypeArgument> astTypeArguments) {
    List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
    for (ASTTypeArgument astTypeArgument : astTypeArguments) {
      if (astTypeArgument instanceof ASTWildcardType) {
        ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;

        // Three cases can occur here: lower bound, upper bound, no bound
        if (astWildcardType.isPresentLowerBound() || astWildcardType.isPresentUpperBound()) {
          // We have a bound.
          // Examples: Set<? extends Number>, Set<? super Integer>

          // new bound
          boolean lowerBound = astWildcardType.isPresentLowerBound();
          ASTType typeBound = lowerBound
              ? astWildcardType.getLowerBound()
              : astWildcardType.getUpperBound();
          int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
          JTypeReference<? extends JTypeSymbol> typeBoundSymbolReference = new JavaTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
              currentScope().get(), dimension);
          ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
              typeBoundSymbolReference);

          // init bound
          addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound, currentScope().get());

          actualTypeArguments.add(actualTypeArgument);
        }
        else {
          // No bound. Example: Set<?>
          actualTypeArguments.add(new ActualTypeArgument(false, false, null));
        }
      }
      else if (astTypeArgument instanceof ASTType) {
        // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
        ASTType astTypeNoBound = (ASTType) astTypeArgument;
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
        JTypeReference<? extends JTypeSymbol> typeArgumentSymbolReference = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
            currentScope().get(), dimension);

        addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound,
            currentScope().get());

        actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
      }
      else {
        Log.error("0xMA073 Unknown type argument " + astTypeArgument + " of type "
            + typeReference);
      }
    }
    typeReference.setActualTypeArguments(actualTypeArguments);
  }

  protected JTypeSymbolsHelper.JTypeReferenceFactory<JavaTypeSymbolReference> typeRefFactory = (name, scope,
      dim) -> new JavaTypeSymbolReference(name, scope, dim);

  protected void addTypeArgumentsToComponent(ComponentSymbolReference typeReference,
      ASTType astType) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.getTypeArgumentsOpt().isPresent()) {
        return;
      }
      setActualTypeArgumentsOfCompRef(typeReference,
          astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypeList()) {
        /* ASTComplexReferenceType represents types like class or interface
         * types which always have ASTSimpleReferenceType as qualification. For
         * example: a.b.c<Arg>.d.e<Arg> */
        setActualTypeArgumentsOfCompRef(typeReference,
            astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
      }
    }
  }

  private void addSubComponentSymbols(ASTSubComponent node) {
    String referencedCompName = TypesPrinter
        .printTypeWithoutTypeArgumentsAndDimension(node.getType());

    // String refCompPackage = Names.getQualifier(referencedCompName);

    String simpleCompName = Names.getSimpleName(referencedCompName);

    ComponentSymbolReference componentTypeReference = new ComponentSymbolReference(
        referencedCompName,
        currentScope().get());
    // actual type arguments
    addTypeArgumentsToComponent(componentTypeReference, node.getType());

    // ref.setPackageName(refCompPackage);
    List<ASTExpression> configArgs = new ArrayList<>();

    for (ASTExpression arg : node.getArgumentsList()) {
      arg.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(arg);
      configArgs.add(arg);
    }

    // instances
    if (!node.getInstancesList().isEmpty()) {
      // create instances of the referenced components.
      for (ASTSubComponentInstance i : node.getInstancesList()) {
        createInstance(i.getName(), i, componentTypeReference, configArgs);
      }
    }
    else {
      // auto instance because instance name is missing
      createInstance(StringTransformations.uncapitalize(simpleCompName), node,
          componentTypeReference, configArgs);
    }

    node.setEnclosingScope(currentScope().get());
  }

  /**
   * Creates the instance and adds it to the symTab.
   */
  private void createInstance(String name, ASTNode node,
      ComponentSymbolReference componentTypeReference,
      List<ASTExpression> configArguments) {
    ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
        componentTypeReference);
    configArguments.forEach(v -> instance.addConfigArgument(v));
    // create a subscope for the instance
    // setLinkBetweenSymbolAndNode(instance, node);
    addToScopeAndLinkWithNode(instance, node);
    // remove the created instance's scope
    // removeCurrentScope();
  }

  /**
   * END Adapted from MontiArc
   */

}



