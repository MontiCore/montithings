// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis._cocos.ArcBasisASTPortAccessCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ComponentInstanceExists implements ArcBasisASTPortAccessCoCo {

  @Override
  public void check(ASTPortAccess node) {

    node.getComponent();
    /*
    String implComp = node.getImplementationComponent().getQName();
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(implComp);

    if (!comp.isPresent()) {
      Log.error(String.format(BindingsError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
    }*/
  }
}
