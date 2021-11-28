// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._symboltable.MontiThingsArtifactScope;
import montithings.util.GenericBindingUtil;
import montithings.util.MontiThingsError;

import java.util.HashSet;
import java.util.Set;

import static montithings.util.GenericBindingUtil.getComponentFromString;

public class InterfaceImplementedCorrectly implements MontiThingsASTMTComponentTypeCoCo {
  @Override
  public void check(ASTMTComponentType node) {
    if (node.isPresentMTImplements()){
      Set<String> names = new HashSet<>(node.getMTImplements().getNameList());
      if (node.getMTImplements().getNameList().size() > names.size()) {
        Log.warn(String.format(
                MontiThingsError.SAME_INTERFACE_MULTIPLE_TIMES.toString(),
                node.getName()));
      }
      for (String name : names) {
        if (node.getEnclosingScope() instanceof MontiThingsArtifactScope){
          ComponentTypeSymbol interfaceComponent =
                  getComponentFromString((MontiThingsArtifactScope) node.getEnclosingScope(), name);
          if (interfaceComponent == null){
            Log.error(String.format(
                    MontiThingsError.TYPE_NOT_FOUND.toString(),
                    name, node.getMTImplements().get_SourcePositionStart()));
          }
          else {
            if (!GenericBindingUtil.canImplementInterface(node.getSymbol(), interfaceComponent)) {
              Log.error(String.format(
                      MontiThingsError.NOT_FITS_INTERFACE.toString(),
                      node, node.getName(), node.getName(),
                      node.get_SourcePositionStart().toString()));
            }
            if (!((ASTMTComponentType) interfaceComponent.getAstNode())
                    .getMTComponentModifier().isInterface()){
              Log.error(
                      String.format(
                              MontiThingsError.NOT_INTERFACE.toString(),
                              "typeName", node.getName(), node.getName(),
                              node.get_SourcePositionStart().toString()));
            }
          }
        }
      }
    }
  }
}
