// (c) https://github.com/MontiCore/monticore

// CoCo to check if the second qualified name(s) of a tag refer to MontiThings components.
package tagging.cocos;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import tagging._ast.ASTTag;
import tagging._ast.ASTTagging;
import tagging._cocos.TaggingASTTaggingCoCo;

public class SecondNameIsAComponent implements TaggingASTTaggingCoCo {

  @Override
  public void check(ASTTagging tagging) {
    //Check if the main component even exists.
    if (!tagging.getSymbol().getMainComponentSymbol().isPresent()) {
      Log.error(
        String.format("0xTAG0003 The main component '%s' does not exist.", tagging.getName()),
        tagging.get_SourcePositionStart());
    }

    //Check each tag to see if the components exist.
    for (ASTTag tag : tagging.getTagList()) {
      for (ASTMCQualifiedName component : tag.getComponentsList()) {
        if (!tag.getSymbol().getComponentSymbolList()
          .get(tag.getComponentsList().indexOf(component)).isPresent()) {
          Log.error(
            String.format("0xTAG0004 The component '%s' does not exist.", component.getQName()),
            tagging.get_SourcePositionStart());
        }
      }
    }
  }
}
