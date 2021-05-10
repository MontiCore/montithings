// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

import de.monticore.utils.Names;

public class CompConfigSymbol extends CompConfigSymbolTOP {
  public CompConfigSymbol(String name) {
    super(name);
  }

  /**
   * Gives the qualified name of the symbol.
   * The qualified symbol name does not contain platform information.
   * E.g. this.name = component_platform => this.fullname = package.component.
   *
   * @return Qualified name without platform information.
   */
  @Override
  public String getFullName() {
    if (fullName == null) {
      fullName = determineFullName();
      fullName = Names.getQualifier(fullName) + "." + this.getAstNode().getName();
    }
    return fullName;
  }
}
