// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._cocos.*;
import montithings._cocos.MontiThingsCoCoChecker;
import portextensions.cocos.PortsInBatchStatementAreIncoming;
import portextensions.cocos.PortsInSyncGroupAreIncoming;
import portextensions.cocos.SyncGroupIsNoSubset;

/**
 * Context Conditions for MontiThings language
 */
public class MontiThingsCoCos {
  public static MontiThingsCoCoChecker createChecker() {
    final MontiThingsCoCoChecker checker = new MontiThingsCoCoChecker();
    return checker
        // ARCBASIS
        .addCoCo(new PortUsage())
        .addCoCo(new NamesCorrectlyCapitalized())
        .addCoCo(new CircularInheritance())
        .addCoCo(new NamesCorrectlyCapitalized())
        .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
        .addCoCo(new ConnectorSourceAndTargetExistAndFit())
        .addCoCo(new ConfigurationParametersCorrectlyInherited())
        .addCoCo(new InnerComponentNotExtendsDefiningComponent())
        .addCoCo(new ComponentInstanceTypeExists())
        .addCoCo(new FieldTypeExists())
        .addCoCo(new InheritedComponentTypeExists())
        .addCoCo(new InnerComponentNotExtendsDefiningComponent())
        .addCoCo(new ParameterTypeExists())
        .addCoCo(new PortUniqueSender())

        // MONTITHINGS
        .addCoCo(new TimeSyncInSubComponents())
        .addCoCo(new MaxOneUpdateInterval())
        .addCoCo(new SyncGroupIsNoSubset())
        .addCoCo(new PortsInSyncGroupAreIncoming())
        .addCoCo(new PortsInBatchStatementAreIncoming())
        ;
  }
}
