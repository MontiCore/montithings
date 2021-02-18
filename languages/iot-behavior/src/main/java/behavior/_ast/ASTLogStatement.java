// (c) https://github.com/MontiCore/monticore
package behavior._ast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASTLogStatement extends ASTLogStatementTOP {
  public List<String> getReferencedVariables() {
    String input = this.getStringLiteral().getSource();
    Matcher m = Pattern.compile("\\$\\w*").matcher(input);
    List<String> words = new ArrayList<>();
    while (m.find())
    {
      words.add(input.substring(m.start()+1, m.end())) ;
    }
    return words;
  }
}
