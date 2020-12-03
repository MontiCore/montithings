/* (c) https://github.com/MontiCore/monticore */
package mtconfig._symboltable;

import de.monticore.utils.Names;

public class CompConfigSymbol extends CompConfigSymbolTOP {
  public CompConfigSymbol(String name) {
    super(name);
  }

  @Override
  public  String getFullName ()  {
    if (fullName == null) {
      fullName = determineFullName();
      fullName = Names.getQualifier(fullName)+"."+this.getAstNode().getName();
    }
    return fullName;
  }
}
