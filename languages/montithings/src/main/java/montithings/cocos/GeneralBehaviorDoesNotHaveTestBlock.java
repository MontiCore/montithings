package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._cocos.MontiThingsASTBehaviorCoCo;
import montithings.util.MontiThingsError;

public class GeneralBehaviorDoesNotHaveTestBlock implements MontiThingsASTBehaviorCoCo {
    @Override
    public void check(ASTBehavior node) {
        if (node.isEmptyNames() && node.isPresentTestBlock()) {
            Log.error(String.format(MontiThingsError.GENERAL_BEHAVIOR_HAS_TEST_BLOCK.toString()));
        }
    }
}
