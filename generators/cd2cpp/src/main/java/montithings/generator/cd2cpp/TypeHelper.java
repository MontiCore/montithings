// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

public class TypeHelper {

  private String _package;

  public TypeHelper(String _package) {
    this._package = _package.replace(".", "::");
  }

  public String printType(TypeSymbol type) {
    return "montithings::" + _package + "::" + type.getName();
  }
}
