// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTPortAccessCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import jdk.internal.org.jline.utils.Log;

import java.util.Optional;

public class ComponentInstanceExists implements ArcBasisASTPortAccessCoCo {

  @Override
  public void check(ASTPortAccess node) {
    if (node.isPresentComponent()) {
      String componentInterfaceName = node.getComponent();
      Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(componentInterfaceName);

      if (!comp.isPresent()) {
        Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), comp));
      }
      //Log.error(String.format(SD4ComponentTestingError.NO_MODEL_IMPLEMENTATION.toString(), comp));
    }


    //TODO: eventuell in Check Methode von ASTSD4Artifact alle Nodes testen (dort ist der Package Name vorhanden)
    //Element mit allen AST Elementen
    //node.getEnclosingScope().getDiagramSymbols().get("MainTest")
  }
}
