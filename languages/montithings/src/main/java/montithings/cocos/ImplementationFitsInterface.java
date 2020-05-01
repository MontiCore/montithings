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
      if (GenericBindingUtil.getGenericBindings(node).containsKey(type.getName(0))) {
        continue;
      }
      ComponentSymbol componentSymbol = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(), type.getName(0));
      if (componentSymbol == null || !componentSymbol.getAstNode().isPresent()) {
        Log.error("Type " + type.getName(0) + " could not be found.", subComponent.get_SourcePositionStart());
      }
      // only subComponents that assign an implementation to interface components are relevant,
      // so we first get the needed data to decide.
      if (type.getTypeArgumentsOpt().isPresent()) {
        List<ASTTypeVariableDeclaration> generics = new ArrayList<>();
        if (((ASTComponent) componentSymbol.getAstNode().get()).getHead().getGenericTypeParametersOpt().isPresent()) {

          ASTComponent nodeAssigningGenerics = (ASTComponent) componentSymbol.getAstNode().get();
          generics = nodeAssigningGenerics.getHead().getGenericTypeParameters().getTypeVariableDeclarationList();

          List<ASTTypeArgument> implementations = ((ASTSimpleReferenceType) subComponent.getType()).getTypeArguments().getTypeArgumentList();
          // we match the potential implementation to a potential interface
          for (int i = 0; i < implementations.size(); i++) {
            if (generics.get(i).getUpperBoundList().size() > 0) {
              // is an implementation of the generic required?
              boolean needsImplementation = false;
              // all interface components for the generic, from which one needs to be implemented
              List<ComponentSymbol> interfaceComps = new ArrayList<>();
              for (ASTComplexReferenceType complexReferenceType : generics.get(i).getUpperBoundList()) {
                ComponentSymbol interfaceComp = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) nodeAssigningGenerics.getEnclosingScopeOpt().get(),
                    complexReferenceType.getSimpleReferenceType(0).getName(0));
                if (interfaceComp != null && ((montithings._symboltable.ComponentSymbol) interfaceComp).isInterfaceComponent()) {
                  needsImplementation = true;
                  interfaceComps.add(interfaceComp);
                }
              }
              if (needsImplementation) {
                //check that the given name corresponds to an existing implementing component
                ComponentSymbol implementation = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(), ((ASTSimpleReferenceType) implementations.get(i)).getName(0));
                if (implementation == null) {
                  Log.error("Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " does not exist." + "Is an resolveable implementing component model available?", subComponent.getInstances(0).get_SourcePositionStart());
                }
                //check that the implementing component is not an interface component
                if (((montithings._symboltable.ComponentSymbol) implementation).isInterfaceComponent()) {
                  Log.error("Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " can not be an interface component.", subComponent.getInstances(0).get_SourcePositionStart());
                }
                //check that the implementing component is compatible to any given interface component.
                if (!interfaceComps.stream().anyMatch(interfaceC -> canImplementInterface((ASTComponent) implementation.getAstNode().get(), (ASTComponent) interfaceC.getAstNode().get()))) {
                  Log.error("Implementation Component " + ((ASTSimpleReferenceType) implementations.get(i)).getName(0) + " of SubComponent " + subComponent.getInstances(0).getName() + " in component " + node.getName() + " does not meet required interface component specification.", subComponent.getInstances(0).get_SourcePositionStart());
                }
              }
            }
          }
        }
      }
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
}