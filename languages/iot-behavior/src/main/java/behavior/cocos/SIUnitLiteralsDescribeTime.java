package behavior.cocos;

import behavior._ast.ASTAfterStatement;
import behavior._cocos.BehaviorASTAfterStatementCoCo;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunits.utility.UnitFactory;
import de.monticore.siunits.utility.UnitPrettyPrinter;
import de.se_rwth.commons.logging.Log;

public class SIUnitLiteralsDescribeTime implements BehaviorASTAfterStatementCoCo {
  @Override
  public void check(ASTAfterStatement node) {
    //TODO: implement
    ASTSIUnit siUnit = node.getSIUnitLiteral().getSIUnit();
    //if(UnitFactory.createBaseUnit(siUnit).equals()){
      //Log.error("test");
    //}
  }
}
