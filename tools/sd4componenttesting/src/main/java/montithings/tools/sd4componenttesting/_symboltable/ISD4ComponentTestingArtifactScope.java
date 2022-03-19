// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;

public  interface ISD4ComponentTestingArtifactScope extends ISD4ComponentTestingArtifactScopeTOP {
  public ComponentTypeSymbol getMainComponentTypeSymbol();
  public void setMainComponentTypeSymbol(ComponentTypeSymbol mainComponentTypeSymbol);
}
