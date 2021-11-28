// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeHelper {

  private String _package;

  public TypeHelper(String _package) {
    this._package = _package.replace(".", "::");
  }

  public String printType(TypeSymbol type) {
    return "montithings::" + _package + "::" + type.getName();
  }

  public static String printType(String typeName) {
    return typeName + "::" + typeName;
  }

  public static final List<String> primitiveTypes = new ArrayList<>(
    Arrays.asList("boolean", "byte", "char", "double", "float",
      "int", "long", "short", "String"));

  public boolean isPrimitive(TypeSymbol type) {
    return primitiveTypes.contains(type.getName());
  }
}
