// (c) https://github.com/MontiCore/monticore

// CoCo to check if the first qualified name of a tag refers to a feature in the feature diagram of the same name.
package tagging.cocos;

import de.se_rwth.commons.logging.Log;
import tagging._ast.ASTTag;
import tagging._ast.ASTTagging;
import tagging._cocos.TaggingASTTaggingCoCo;

public class FirstNameIsAFeature implements TaggingASTTaggingCoCo {

  @Override
  public void check(ASTTagging tagging) {
    //Check if the feature diagram even exists.
    if (!tagging.getSymbol().getFeatureDiagramSymbol().isPresent()) {
      Log.error(
        String.format("0xTAG0001 The feature diagram '%s' does not exist.", tagging.getName()),
        tagging.get_SourcePositionStart());
    }

    //Check each tag to see if the feature in the feature diagram exists.
    for (ASTTag tag : tagging.getTagList()) {
      if (!tag.getSymbol().getFeatureSymbol().isPresent()) {
        Log.error(String.format("0xTAG0002 The feature '%s' does not exist in the feature diagram.",
          tag.getFeature().getQName()), tagging.get_SourcePositionStart());
      }
    }
  }
}
