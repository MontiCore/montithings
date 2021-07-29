// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTEveryTag;
import mtconfig._ast.ASTPortTemplateTag;
import mtconfig.util.MTConfigError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that at most one "every" tag exists for every port.
 */
public class OnlyOneEveryPerPort implements MTConfigASTPortTemplateTagCoCo {

  @Override public void check(ASTPortTemplateTag node) {
    List<ASTEveryTag> tags = node.getSinglePortTagList().stream()
      .filter(t -> t instanceof ASTEveryTag)
      .map(e -> (ASTEveryTag) e)
      .collect(Collectors.toList());

    if (tags.size() > 1) {
      Log.error(String.format(MTConfigError.ONLY_ONE_EVERY.toString(), node.getName()));
    }
  }
}
