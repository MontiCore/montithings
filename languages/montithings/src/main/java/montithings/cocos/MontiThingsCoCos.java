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
      .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
      .addCoCo(new ConnectorSourceAndTargetExistAndFit())
      .addCoCo(new ConfigurationParametersCorrectlyInherited())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      .addCoCo(new ComponentInstanceTypeExists())
      .addCoCo(new FieldTypeExists())
      .addCoCo(new InheritedComponentTypeExists())
      .addCoCo(new ParameterTypeExists())
      .addCoCo(new PortUniqueSender())
      // we do not import the SubComponentsConnected CoCo from ArcBasis here.
      // Instead we use the PortConnection which will be added by the
      // GeneratorTool. Adding it here and replacing it in the generator is not
      // really an option because MontiCore stores the CoCos in a gazillion
      // different Collections and provides no getters

      // MONTITHINGS
      .addCoCo(new TimeSyncInSubComponents())
      .addCoCo(new MaxOneUpdateInterval())
      .addCoCo(new SyncGroupIsNoSubset())
      .addCoCo(new PortsInSyncGroupAreIncoming())
      .addCoCo(new PortsInBatchStatementAreIncoming())
      .addCoCo(new NameExpressionsAreResolvable())
      ;
  }
}
