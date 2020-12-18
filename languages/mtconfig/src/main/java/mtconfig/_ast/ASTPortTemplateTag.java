// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

import arcbasis._symboltable.PortSymbol;

public class ASTPortTemplateTag extends ASTPortTemplateTagTOP {

  PortSymbol portSymbol;

  public PortSymbol getPortSymbol() {
    return portSymbol;
  }

  public void setPortSymbol(PortSymbol portSymbol) {
    this.portSymbol = portSymbol;
  }

  @Override public String getName() {
    return getPort();
  }
}
