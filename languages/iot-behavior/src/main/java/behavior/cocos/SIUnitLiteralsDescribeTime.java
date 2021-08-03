// (c) https://github.com/MontiCore/monticore
package behavior.cocos;

import behavior._ast.ASTAfterStatement;
import behavior._cocos.BehaviorASTAfterStatementCoCo;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunits.utility.UnitFactory;
import de.se_rwth.commons.logging.Log;

public class SIUnitLiteralsDescribeTime implements BehaviorASTAfterStatementCoCo {
  @Override
  public void check(ASTAfterStatement node) {
    ASTSIUnit siUnit = node.getSIUnitLiteral().getSIUnit();

    if (!UnitFactory.createBaseUnit("s")
      .isCompatible(UnitFactory.createUnit(SIUnitsPrettyPrinter.prettyprint(siUnit)))) {
      Log.error("0xMT02324 SI unit '" + SIUnitsPrettyPrinter.prettyprint(siUnit)
        + "' in 'after' statement is not a time unit.");
    }
  }
}
