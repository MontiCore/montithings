// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Optional;

/**
 * Helps to distinguish which attributes are derived from interface classes
 */
public class FieldHelper {

  public static boolean hasFieldFromInterface(CDTypeSymbol type) {
    for (FieldSymbol field : type.getFieldList()) {
      if (isFromInterface(type, field)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isFromInterface(CDTypeSymbol type, FieldSymbol field) {
    if (type.getInterfaceList().isEmpty()) {
      MCBasicTypesFullPrettyPrinter p = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
      for (ASTMCObjectType interfaceType : type.getAstNode().getInterfaceList()) {
        Optional<CDTypeSymbol> interfaceSymbol = type.getEnclosingScope().resolveCDType(interfaceType.printType(p));
        if (interfaceSymbol.isPresent()) {
          for (FieldSymbol fieldSymbol : interfaceSymbol.get().getFieldList()) {
            if (fieldSymbol.getName().equals(field.getName()) &&
              fieldSymbol.getType().print().equals(field.getType().print())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public static String getInterface (CDTypeSymbol type, FieldSymbol field) {
    if (isFromInterface(type, field)) {
      MCBasicTypesFullPrettyPrinter p = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
      for (ASTMCObjectType interfaceType : type.getAstNode().getInterfaceList()) {
        Optional<CDTypeSymbol> interfaceSymbol = type.getEnclosingScope().resolveCDType(interfaceType.printType(p));
        if (interfaceSymbol.isPresent()) {
          for (FieldSymbol fieldSymbol : interfaceSymbol.get().getFieldList()) {
            if (fieldSymbol.getName().equals(field.getName()) &&
              fieldSymbol.getType().print().equals(field.getType().print())) {
              return TypeHelper.printType(interfaceSymbol.get().getName());
            }
          }
        }
      }
    }
    return "";
  }
}
