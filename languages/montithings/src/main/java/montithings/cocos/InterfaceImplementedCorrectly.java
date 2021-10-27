// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.GenericBindingUtil;
import montithings.util.MontiThingsError;

import static montithings.util.GenericBindingUtil.getComponentFromString;

public class InterfaceImplementedCorrectly implements MontiThingsASTMTComponentTypeCoCo {
  @Override
  public void check(ASTMTComponentType node) {
    if (node.isPresentMTImplements()){
      if (node.getEnclosingScope() instanceof MontiThingsArtifactScope){
        ComponentTypeSymbol interfaceComponent =
          getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(), node.getMTImplements().getName());
        if (!GenericBindingUtil.canImplementInterface(node.getSymbol(), interfaceComponent)) {
          /*Log.error(String.format(
                  MontiThingsError.NOT_FITS_INTERFACE.toString(),
                  typeName, subComponent.getInstanceName(0), node.getName(),
                  subComponent.getComponentInstance(0).get_SourcePositionStart().toString()))*/
        }
      }
    }
  }
}
