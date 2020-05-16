// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTSubComponent;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcArtifactScope;
import montithings._ast.ASTComponent;
import montithings._cocos.MontiThingsASTComponentCoCo;
import montithings.helper.GenericBindingUtil;

import java.util.List;
import java.util.Map;

/**
 * Checks that Interface component exists.
 * @author Julian Krebber
 * @version , 01.05.2020
 */
public class InterfaceExists implements MontiThingsASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if(!node.getEnclosingScopeOpt().isPresent()){
      Log.error("0xMT140 No Component enclosing Scope. Was SymbolTable initialized?");
    }
    //for every generic subcomponent, check that it has an existing interface.
    Map<String, ASTSimpleReferenceType> genericToInterface = GenericBindingUtil.getGenericBindings(node);
    for (ASTSubComponent subComp : node.getSubComponents()) {
      ASTReferenceType type = subComp.getType();
      String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type);
      // does the subcomponent use a generic type?
      if (genericToInterface.containsKey(typeName)) {
        // each generic type requires interface components, so we collect their names
        List<String> interfaceNames = genericToInterface.get(typeName).getNameList();
        for(String interfaceName : interfaceNames ){
          ComponentSymbol interfaceComp = GenericBindingUtil.getComponentFromString((MontiArcArtifactScope) node.getEnclosingScopeOpt().get(),interfaceName);
          // from the name we check that component exists and that it is really an interface component
          if(interfaceComp==null
              ||!((montithings._symboltable.ComponentSymbol)interfaceComp).isInterfaceComponent()){
            Log.error("0xMT141 Generic " + typeName + " for component instance "+
                subComp.getInstances(0).getName() + " in component "+ node.getName() + " does not extend only interface models. "
                + "Is an resolveable interface component model available?",subComp.getInstances(0).get_SourcePositionStart());
          }
        }
      }
    }
  }
}