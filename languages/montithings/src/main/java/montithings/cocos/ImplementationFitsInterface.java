// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.types.types._ast.*;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTSubComponent;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcArtifactScope;
import montithings._ast.ASTComponent;
import montithings._ast.ASTPort;
import montithings._cocos.MontiThingsASTComponentCoCo;
import montithings.helper.GenericBindingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks if implementation of interface components is correct.
 * @author Julian Krebber
 * @version , 01.05.2020
 */
public class ImplementationFitsInterface implements MontiThingsASTComponentCoCo {

  /**
   * Checks for every subcomponent using implementation types, that they exists and they are not an interface component
   * and that they fit the interface component type.
   *
   * @param node subcomponents to be checked
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getEnclosingScopeOpt().isPresent()) {
      Log.error("No Component enclosing Scope. Was SymbolTable initialized?");
    }
    // iterate over every subComponent and collect their type aswell as their type arguments
    for (ASTSubComponent subComponent : node.getSubComponents()) {
      ASTSimpleReferenceType type = (ASTSimpleReferenceType) subComponent.getType();
      checkBindingAssignment(type,node,subComponent);
    }
  }

  /**
   * Checks if a component can formally implement an interface component, by
   * checking if a component has the same ports as another component.
   *
   * @param implementComp component implementing an interface component.
   * @param interfaceComp interface component.
   * @return if all ports match completely.
   */
  private static boolean canImplementInterface(ASTComponent implementComp, ASTComponent interfaceComp) {
    boolean result = true;
    List<ASTPort> portsImpl = implementComp.getPorts().stream().map(p -> (montithings._ast.ASTPort) p).collect(Collectors.toList());
    List<ASTPort> portsInter = interfaceComp.getPorts().stream().map(p -> (montithings._ast.ASTPort) p).collect(Collectors.toList());

    for (ASTPort portInter : portsInter) {
      if (portsImpl.stream().noneMatch(pImp -> pImp.deepEquals(portInter))) {
        result = false;
      }
    }
    for (ASTPort portImpl : portsImpl) {
      if (portsInter.stream().noneMatch(pImp -> pImp.deepEquals(portImpl))) {
        result = false;
      }
    }
    return result;
  }

  /**
   * Checks if the given type is a generic of the node and if it uses the same given interface.
   * @param implementingType name of the potentially generic type of the given node.
   * @param node component that is checked for the generic type.
   * @param interfaceComps interface components that should contain the interface components of the generic (if it exists).
   * @param subComponentName name of the subcomponent the generic is used in. Only used for clearer error message.
   * @return true, if it is an valid generic type for binding implementation. false if it is not an generic of the given node.
   * logs error if it is an generic of the node, but is not valid.
   */
  protected static boolean genericInterfaceType(String implementingType, ASTComponent node, List<ComponentSymbol> interfaceComps, String subComponentName) {
    Map<String, ASTSimpleReferenceType> nodeGenerics = GenericBindingUtil.getGenericBindings(node);
    if(nodeGenerics.containsKey(implementingType)){
      List<ComponentSymbol> interfaceCompOthers = new ArrayList<>();
      for(String interfaceCompName : nodeGenerics.get(implementingType).getNameList()) {
        ComponentSymbol interfaceCompOther = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(),
            interfaceCompName);
        if(interfaceCompOther==null) {
          Log.error("0xMT146 Interface component " + interfaceCompName
                  + " of Generic " +  implementingType + " in component " + node.getName()
                  + " not found. "
                  + "Is the interface component model available and resolve able?",
              node.getHead().get_SourcePositionStart());
          return true;
        }
        interfaceCompOthers.add(interfaceCompOther);
      }
      for(ComponentSymbol interfaceComp : interfaceCompOthers) {
        if (!interfaceComps.contains(interfaceComp)) {
          Log.error("0xMT147 Generic " + implementingType +
              " of SubComponent " + subComponentName +
              " in component " + node.getName() +
              " does not allow the interface component " + interfaceComp.getName() + "."
              + "Is a valid resolve able interface component model available and does the generic extend it?",
              node.getHead().get_SourcePositionStart());
        }
      }
      return true;
    }
    else if(node.getHead().getGenericTypeParametersOpt().isPresent()&&
        node.getHead().getGenericTypeParameters().getTypeVariableDeclarationList().stream().anyMatch(g -> g.getName().equals(implementingType))){
      Log.error("0xMT148 Generic " + implementingType
              + " of SubComponent " + subComponentName + " in component " + node.getName()
              + " requires an interface component."
              + "Does the generic extend an component?",
          node.getHead().get_SourcePositionStart());
      return true;
    }
    return false;
  }

  private void checkBindingAssignment(ASTSimpleReferenceType type,ASTComponent node, ASTSubComponent subComponent) {
    if (!GenericBindingUtil.getGenericBindings(node).containsKey(type.getName(0))) {
      ComponentSymbol componentSymbol = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(), type.getName(0));
      if (componentSymbol == null || !componentSymbol.getAstNode().isPresent()) {
        Log.error("0xMT142 Type " + type.getName(0) + " could not be found.", subComponent.get_SourcePositionStart());
      }
      else {
        // only subComponents that assign an implementation to interface components are relevant,
        // so we first get the needed data to decide.
        if (type.getTypeArgumentsOpt().isPresent()) {
          List<ASTTypeVariableDeclaration> generics;
          if (((ASTComponent) componentSymbol.getAstNode().get()).getHead().getGenericTypeParametersOpt().isPresent()) {

            ASTComponent nodeAssigningGenerics = (ASTComponent) componentSymbol.getAstNode().get();
            generics = nodeAssigningGenerics.getHead().getGenericTypeParameters().getTypeVariableDeclarationList();

            List<ASTTypeArgument> implementations = type.getTypeArguments().getTypeArgumentList();
            // we match the potential implementation to a potential interface
            for (int i = 0; i < implementations.size(); i++) {
              if (generics.get(i).getUpperBoundList().size() > 0) {
                // is an implementation of the generic required?
                boolean needsImplementation = false;
                // all interface components for the generic, from which one needs to be implemented
                List<ComponentSymbol> interfaceComps = new ArrayList<>();
                for (ASTComplexReferenceType complexReferenceType : generics.get(i).getUpperBoundList()) {
                  ComponentSymbol interfaceComp = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) nodeAssigningGenerics.getEnclosingScopeOpt().get(), complexReferenceType.getSimpleReferenceType(0).getName(0));
                  if (interfaceComp != null && ((montithings._symboltable.ComponentSymbol) interfaceComp).isInterfaceComponent()) {
                    needsImplementation = true;
                    interfaceComps.add(interfaceComp);
                  }
                }
                if (needsImplementation) {
                  //check that the given name corresponds to an existing implementing component
                  ComponentSymbol implementation = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(), ((ASTSimpleReferenceType) implementations.get(i)).getName(0));
                  if (!genericInterfaceType(((ASTSimpleReferenceType) implementations.get(i)).getName(0), node, interfaceComps, subComponent.getInstances(0).getName())) {
                    if (implementation == null) {
                      Log.error("0xMT143 Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " does not exist." + "Is an resolveable implementing component model available?", subComponent.getInstances(0).get_SourcePositionStart());
                    }
                    //check that the implementing component is not an interface component
                    else if (((montithings._symboltable.ComponentSymbol) implementation).isInterfaceComponent()) {
                      Log.error("0xMT144 Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " can not be an interface component.", subComponent.getInstances(0).get_SourcePositionStart());
                    }
                    //check that the implementing component is compatible to any given interface component.
                    else if (interfaceComps.stream().noneMatch(interfaceC -> canImplementInterface((ASTComponent) implementation.getAstNode().get(), (ASTComponent) interfaceC.getAstNode().get()))) {
                      Log.error("0xMT145 Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " does not meet required interface component specification.", subComponent.getInstances(0).get_SourcePositionStart());
                    }
                    else {
                      checkBindingAssignment((ASTSimpleReferenceType) implementations.get(i), node, subComponent);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}