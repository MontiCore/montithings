package componenttest.cocos;

import componenttest._ast.ASTWaitStatement;
import componenttest._cocos.ComponentTestASTWaitStatementCoCo;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunits.utility.UnitFactory;
import de.se_rwth.commons.logging.Log;

public class SIUnitsInWaitStatementDescribeTime implements ComponentTestASTWaitStatementCoCo {
  @Override
  public void check(ASTWaitStatement node) {
    ASTSIUnit siUnit = node.getSIUnitLiteral().getSIUnit();

    if (!UnitFactory.createBaseUnit("s")
      .isCompatible(UnitFactory.createUnit(SIUnitsPrettyPrinter.prettyprint(siUnit)))) {
      Log.error("0xMT02325 SI unit '" + SIUnitsPrettyPrinter.prettyprint(siUnit)
        + "' in wait statement is not a time unit.");
    }
  }
}
