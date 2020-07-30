// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.GenericBindingUtil;
import montithings.util.MontiThingsError;

import java.util.Map;

/**
 * Checks that Interface component exists.
 *
 * @author Julian Krebber
 * @version , 01.05.2020
 */
public class InterfaceExists implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(ASTComponentType node) {
    if (node.getEnclosingScope() == null) {
      Log.error(MontiThingsError.NO_ENCLOSING_SCOPE.toString());
    }
    //for every generic subcomponent, check that it has an existing interface.
    Map<String, String> genericToInterface = GenericBindingUtil.getGenericBindings(node);
    for (ASTComponentInstantiation subComp : node.getSubComponentInstantiations()) {
      ASTMCType type = subComp.getMCType();
      String typeName = GenericBindingUtil.printSimpleType(type);
      // does the subcomponent use a generic type?
      if (genericToInterface.containsKey(typeName)) {
        // each generic type requires interface components, so we collect their names
        String interfaceName = genericToInterface.get(typeName);
        ComponentTypeSymbol interfaceComp = GenericBindingUtil.getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(), interfaceName);
        // from the name we check that component exists and that it is really an interface component
        if (interfaceComp == null || !((ASTMTComponentType) interfaceComp.getAstNode()).getMTComponentModifier().isInterface()) {
          Log.error(
              String.format(
                  MontiThingsError.NOT_INTERFACE.toString(),
                    typeName, subComp.getInstancesNames().get(0), node.getName(), subComp.get_SourcePositionStart().toString()));
        }
      }
    }
  }
}
