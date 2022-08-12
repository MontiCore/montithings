package montithings.generator.cd2proto.CoCos;

import de.monticore.cd.cocos.CoCoParent;
import de.monticore.cd4analysis._cocos.CD4AnalysisCoCoChecker;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdassociation._cocos.CDAssociationASTCDAssociationCoCo;
import de.monticore.cdassociation._cocos.CDAssociationCoCoChecker;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.monticore.cdbasis._cocos.CDBasisASTCDTypeCoCo;
import de.monticore.cdbasis._cocos.CDBasisCoCoChecker;

public class CD2ProtoCoCos {
    public CDBasisCoCoChecker getCheckerForAllCocos() {
        final CDBasisCoCoChecker checker = new CDBasisCoCoChecker();
        checker.addCoCo(new NoCircleCoCo());
        return checker;
    }
}