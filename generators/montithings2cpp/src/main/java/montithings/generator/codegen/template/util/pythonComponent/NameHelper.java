package montithings.generator.codegen.template.util.pythonComponent;

import java.util.regex.Pattern;

public class NameHelper {
  public static String getLastPart(String name) {
    String[] res = name.split("\\.",2);
    return res[res.length-1];
  }
}
