// (c) https://github.com/MontiCore/monticore
package tagging.cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import tagging._ast.ASTTag;
import tagging._ast.ASTTagging;
import tagging._cocos.TaggingASTTaggingCoCo;

public class NoComponentIsMentionedTwiceInASingleTag implements TaggingASTTaggingCoCo {

  @Override
  public void check(ASTTagging tagging) {
    ASTMCQualifiedName component1;
    ASTMCQualifiedName component2;
    for (ASTTag tag : tagging.getTagList()) {
      for (int i = 0; i < tag.getComponentsList().size() - 1; i++) {
        component1 = tag.getComponents(i);
        for (int j = i + 1; j <= tag.getComponentsList().size() - 1; j++) {
          component2 = tag.getComponents(j);
          if (component1 != component2 && component1.getQName().equals(component2.getQName())) {
            Log.error(String.format(
              "0xTAG0006 The component '%s' is defined more than once in a single tag.",
              component2.getQName()), tagging.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
