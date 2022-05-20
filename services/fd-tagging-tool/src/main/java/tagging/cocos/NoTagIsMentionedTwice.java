// (c) https://github.com/MontiCore/monticore
package tagging.cocos;

import de.se_rwth.commons.logging.Log;
import tagging._ast.ASTTag;
import tagging._ast.ASTTagging;
import tagging._cocos.TaggingASTTaggingCoCo;

public class NoTagIsMentionedTwice implements TaggingASTTaggingCoCo {

  @Override
  public void check(ASTTagging tagging) {
    ASTTag tag1;
    ASTTag tag2;
    for (int i = 0; i < tagging.getTagList().size() - 1; i++) {
      tag1 = tagging.getTagList().get(i);
      for (int j = i + 1; j <= tagging.getTagList().size() - 1; j++) {
        tag2 = tagging.getTagList().get(j);
        if (tag1 != tag2 && tag1.getFeature().getQName().equals(tag2.getFeature().getQName())) {
          Log.error(
            String.format("0xTAG0005 The feature '%s' is mentioned in more than one tagging rule.",
              tag2.getFeature().getQName()),
            tagging.get_SourcePositionStart());
        }
      }
    }
  }
}
