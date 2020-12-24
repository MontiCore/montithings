// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

public class TypesHelper {
  /**
   * @return Corresponding CPP types from input java types
   */
  public static String java2cppTypeString(String type) {
    return java2cppTypeString(type, false);
  }

  public static String java2cppTypeString(String type, boolean preventRecursion) {
    String replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    while (!type.equals(replacedArray)) {
      type = replacedArray;
      replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    }
    type = type.replaceAll("(\\W|^)String(\\W|$)", "$1std::string$2");
    type = type.replaceAll("(\\W|^)Integer(\\W|$)", "$1int$2");
    type = type.replaceAll("(\\W|^)Map(\\W|$)", "$1std::map$2");
    type = type.replaceAll("(\\W|^)Set(\\W|$)", "$1std::set$2");
    type = type.replaceAll("(\\W|^)List(\\W|$)", "$1std::list$2");
    type = type.replaceAll("(\\W|^)Boolean(\\W|$)", "$1bool$2");
    type = type.replaceAll("(\\W|^)boolean(\\W|$)", "$1bool$2");
    type = type.replaceAll("(\\W|^)Character(\\W|$)", "$1char$2");
    type = type.replaceAll("(\\W|^)Double(\\W|$)", "$1double$2");
    type = type.replaceAll("(\\W|^)Float(\\W|$)", "$1float$2");

    if (preventRecursion) {
      return type;
    }

    while (!java2cppTypeString(type, true).equals(type)) {
      type = java2cppTypeString(type);
    }
    return type;
  }

  public static boolean isJavaType(String type) {
    return type.startsWith("String") ||
      type.startsWith("Map") ||
      type.startsWith("Set") ||
      type.startsWith("List") ||
      type.startsWith("Integer") ||
      type.startsWith("int") ||
      type.startsWith("Boolean") ||
      type.startsWith("boolean") ||
      type.startsWith("Character") ||
      type.startsWith("char") ||
      type.startsWith("Double") ||
      type.startsWith("double") ||
      type.startsWith("Float") ||
      type.startsWith("float")
      ;
  }
}
