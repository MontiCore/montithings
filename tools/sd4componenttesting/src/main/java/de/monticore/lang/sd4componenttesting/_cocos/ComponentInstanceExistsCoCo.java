// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ComponentInstanceExistsCoCo implements ArcBasisASTComponentInstanceCoCo {
  @Override
  public void check(ASTComponentInstance node) {
    String implComp = node.getName();
    Optional<ComponentInstanceSymbol> comp = node.getEnclosingScope().resolveComponentInstance(node.getName());

    Optional<ComponentTypeSymbol> comp2 = node.getEnclosingScope().resolveComponentType(node.getName());

    if (!comp.isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
    }
    if (!comp2.isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
    }
    Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), implComp));
  }
/*
  @Override
  public void check(ASTPortAccess node) {
      String packageName = node.getPortSymbol().getPackageName();
      if (packageName.equals("")) {
        Log.error("Blub Foo");
      }
  }*/
}
