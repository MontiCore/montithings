package montithings.generator.cd2proto.CoCos;

import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;

public class CD2ProtoCoCos {
    public CDBasisCoCoChecker getCheckerForAllCocos() {
        final CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
        checker.addCoCo(new NoCircleCoCo());
        return checker;
    }
}