// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;

import java.util.List;

/**
 * TODO
 *
 * @since 12.07.20
 */
public class CppPrettyPrinterUtils {

  protected static String capitalize(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  protected static String isSet(List<PortSymbol> batchPorts, String name) {
    return batchPorts.stream()
      .filter(p -> p.getName().equals(name))
      .findFirst()
      .map(p -> ".size() > 0")
      .orElse("");
  }
}
