// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTPortAccessCoCo;

public class ComponentInstanceExists implements ArcBasisASTPortAccessCoCo {

  @Override
  public void check(ASTPortAccess node) {

    //TODO: evaluate node.getSymbol().getPackageDeclaration()
      String componentInterfaceName = node.getQName();


    //TODO: eventuell in Check Methode von ASTSD4Artifact alle Nodes testen (dort ist der Package Name vorhanden)
    //Element mit allen AST Elementen
    //node.getEnclosingScope().getDiagramSymbols().get("MainTest")
  }
}
