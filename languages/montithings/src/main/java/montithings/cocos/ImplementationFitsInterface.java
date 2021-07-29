// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montithings._ast.ASTMTComponentType;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.GenericBindingUtil;
import montithings.util.MontiThingsError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Checks if implementation of interface components is correct
 */
public class ImplementationFitsInterface implements ArcBasisASTComponentTypeCoCo {

  /**
   * Checks for every subcomponent using implementation types, that they exists and they are not an interface component
   * and that they fit the interface component type.
   *
   * @param node subcomponents to be checked
   */
  @Override
  public void check(ASTComponentType node) {
    if (node.getEnclosingScope() == null) {
      Log.error(MontiThingsError.NO_ENCLOSING_SCOPE.toString());
    }
    // iterate over every subComponent and collect their type aswell as their type arguments
    for (ASTComponentInstantiation subComponent : node.getSubComponentInstantiations()) {
      ASTMCType type = subComponent.getMCType();
      checkBindingAssignment(type, node, subComponent);
    }
  }

  /**
   * Checks if the given type is a generic of the node and if it uses the same given interface.
   *
   * @param implementingType name of the potentially generic type of the given node.
   * @param node             component that is checked for the generic type.
   * @param interfaceComps   interface components that should contain the interface components of the generic (if it exists).
   * @param subComponentName name of the subcomponent the generic is used in. Only used for clearer error message.
   * @return true, if it is an valid generic type for binding implementation. false if it is not an generic of the given node.
   * logs error if it is an generic of the node, but is not valid.
   */
  protected static boolean genericInterfaceType(String implementingType, ASTComponentType node,
    List<ComponentTypeSymbol> interfaceComps, String subComponentName) {
    Map<String, String> nodeGenerics = GenericBindingUtil.getGenericBindings(node);
    if (nodeGenerics.containsKey(implementingType)) {
      List<ComponentTypeSymbol> interfaceCompOthers = new ArrayList<>();
      String interfaceCompName = nodeGenerics.get(implementingType);
      ComponentTypeSymbol interfaceCompOther = GenericBindingUtil
        .getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(),
          interfaceCompName);
      if (interfaceCompOther == null) {
        Log.error(
          String.format(
            MontiThingsError.GENERIC_PARAMTER_INTERFACE_NOT_FOUND.toString(),
            interfaceCompName, implementingType, node.getName(),
            node.getHead().get_SourcePositionStart().toString()));
        return true;
      }
      interfaceCompOthers.add(interfaceCompOther);
      for (ComponentTypeSymbol interfaceComp : interfaceCompOthers) {
        if (!interfaceComps.contains(interfaceComp)) {
          Log.error(
            String.format(
              MontiThingsError.GENERIC_PARAMTER_NOT_FITS_INTERFACE.toString(),
              implementingType, subComponentName, node.getName(), interfaceComp.getName(),
              node.getHead().get_SourcePositionStart().toString()));
          return true;
        }
      }
      return true;
    }
    else if (node.getHead() instanceof ASTGenericComponentHead &&
      ((ASTGenericComponentHead) node.getHead()).getArcTypeParameterList().stream()
        .anyMatch(g -> g.getName().equals(implementingType))) {
      Log.error(String.format(
        MontiThingsError.GENERIC_PARAMETER_NEEDS_INTERFACE.toString(),
        implementingType, subComponentName, node.getName(),
        node.getHead().get_SourcePositionStart().toString()));
      return true;
    }
    return false;
  }

  protected void checkBindingAssignment(ASTMCType type, ASTComponentType node,
    ASTComponentInstantiation subComponent) {
    if (!GenericBindingUtil.getGenericBindings(node)
      .containsKey(GenericBindingUtil.printSimpleType(type))) {
      ComponentTypeSymbol ComponentTypeSymbol = GenericBindingUtil
        .getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(),
          GenericBindingUtil.printSimpleType(type));
      if (ComponentTypeSymbol == null || !ComponentTypeSymbol.isPresentAstNode()) {
        Log.error(String.format(
          MontiThingsError.TYPE_NOT_FOUND.toString(),
          GenericBindingUtil.printSimpleType(type),
          subComponent.get_SourcePositionStart().toString()));
      }
      else {
        // only subComponents that assign an implementation to interface components are relevant,
        // so we first get the needed data to decide.
        if (type instanceof ASTMCGenericType) {
          if (ComponentTypeSymbol.getAstNode().getHead() instanceof ASTGenericComponentHead) {

            ASTComponentType nodeAssigningGenerics = ComponentTypeSymbol.getAstNode();
            List<ASTArcTypeParameter> generics = ((ASTGenericComponentHead) nodeAssigningGenerics
              .getHead()).getArcTypeParameterList();

            List<ASTMCTypeArgument> implementations = ((ASTMCGenericType) type)
              .getMCTypeArgumentList();
            // we match the potential implementation to a potential interface
            for (int i = 0; i < implementations.size(); i++) {
              if (generics.get(i).getUpperBoundList().size() > 0) {
                // is an implementation of the generic required?
                boolean needsImplementation = false;
                // all interface components for the generic, from which one needs to be implemented
                List<ComponentTypeSymbol> interfaceComps = new ArrayList<>();
                for (ASTMCType complexReferenceType : generics.get(i).getUpperBoundList()) {
                  ComponentTypeSymbol interfaceComp = GenericBindingUtil.getComponentFromString(
                    (MontiThingsArtifactScope) nodeAssigningGenerics.getEnclosingScope(),
                    GenericBindingUtil.printSimpleType(complexReferenceType));
                  if (interfaceComp != null && interfaceComp
                    .getAstNode() instanceof ASTMTComponentType
                    && ((ASTMTComponentType) interfaceComp.getAstNode()).getMTComponentModifier()
                    .isInterface()) {
                    needsImplementation = true;
                    interfaceComps.add(interfaceComp);
                  }
                }
                if (needsImplementation && implementations.get(i).getMCTypeOpt().isPresent()) {
                  String typeName = GenericBindingUtil
                    .printSimpleType(implementations.get(i).getMCTypeOpt().get());
                  //check that the given name corresponds to an existing implementing component
                  ComponentTypeSymbol implementation = GenericBindingUtil
                    .getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(),
                      typeName);
                  if (!genericInterfaceType(typeName, node, interfaceComps,
                    subComponent.getInstanceName(0))) {
                    if (implementation == null) {
                      Log.error(String.format(
                        MontiThingsError.IMPLEMENTATION_MISSING.toString(),
                        typeName, subComponent.getInstanceName(0), node.getName(),
                        subComponent.getComponentInstance(0).get_SourcePositionStart().toString()));
                    }
                    //check that the implementing component is not an interface component
                    else if (implementation.getAstNode() instanceof ASTMTComponentType
                      && ((ASTMTComponentType) implementation.getAstNode()).getMTComponentModifier()
                      .isInterface()) {
                      Log.error(String.format(
                        MontiThingsError.INTERFACE_IMPLEMENTS_INTERFACE.toString(),
                        typeName, subComponent.getInstanceName(0), node.getName(),
                        subComponent.getComponentInstance(0).get_SourcePositionStart().toString()));
                    }
                    //check that the implementing component is compatible to any given interface component.
                    else if (interfaceComps.stream().noneMatch(interfaceC -> GenericBindingUtil
                      .canImplementInterface(implementation, interfaceC))) {
                      Log.error(String.format(
                        MontiThingsError.NOT_FITS_INTERFACE.toString(),
                        typeName, subComponent.getInstanceName(0), node.getName(),
                        subComponent.getComponentInstance(0).get_SourcePositionStart().toString()));
                    }
                    else {
                      checkBindingAssignment(implementations.get(i).getMCTypeOpt().get(), node,
                        subComponent);
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