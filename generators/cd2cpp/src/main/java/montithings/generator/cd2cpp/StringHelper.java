// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

/**
 * TODO
 *
 * @since 21.12.20
 */
public class StringHelper {
  public static String toFirstLower(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

  public static String toFirstUpper(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}
