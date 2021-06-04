// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._cocos.*;
import behavior.cocos.SIUnitLiteralsDescribeTime;
import de.monticore.siunittypes4computing._cocos.PrimitiveIsNumericType;
import de.monticore.types.check.TypeCheckResult;
import montiarc.check.MontiArcDerive;
import montithings._cocos.MontiThingsASTBehaviorCoCo;
import montithings._cocos.MontiThingsASTMTEveryBlockCoCo;
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

    // ARCBASIS
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ComponentInstanceTypeExists());
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcDerive(new TypeCheckResult())));
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new FieldTypeExists());
    checker.addCoCo(new InheritedComponentTypeExists());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new NoSubComponentReferenceCycles());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new ParameterTypeExists());
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new PortTypeExists());
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new PortUsage());
    checker.addCoCo(new UniqueIdentifierNames());

    // we do not import the SubComponentsConnected CoCo from ArcBasis here.
    // Instead we use the PortConnection which will be added by the
    // GeneratorTool. Adding it here and replacing it in the generator is not
    // really an option because MontiCore stores the CoCos in a gazillion
    // different Collections and provides no getters

    // SIUNITS
    checker.addCoCo(new PrimitiveIsNumericType());

    // MONTITHINGS
    checker.addCoCo(new MaxOneUpdateInterval());
    checker.addCoCo(new SyncGroupIsNoSubset());
    checker.addCoCo(new PortsInSyncGroupAreIncoming());
    checker.addCoCo(new PortsInBatchStatementAreIncoming());
    checker.addCoCo(MontiThingsTypeCheckCoCo.getCoCo());
    checker.addCoCo(new NameExpressionsAreResolvable());
    checker.addCoCo(new SIUnitLiteralsDescribeTime());
    checker.addCoCo(new OCLExpressionsValid());
    checker.addCoCo(new UnsupportedOperator());
    checker.addCoCo(new NoIncomingPortsInEveryBlocks());
    checker.addCoCo(new LoggedVariablesAreResolvable());
    checker.addCoCo(new PublishReferencesPort());
    checker.addCoCo(new PostcondUsesOnlyOneOutport());
    checker.addCoCo((MontiThingsASTBehaviorCoCo) new DontReadOutports());
    checker.addCoCo((MontiThingsASTMTEveryBlockCoCo) new DontReadOutports());
    checker.addCoCo(new PortsInBehaviorAreUsedCorrectly());

    return checker;
  }
}
