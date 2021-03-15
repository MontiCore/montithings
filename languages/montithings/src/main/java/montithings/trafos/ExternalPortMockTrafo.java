// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTConnector;
import behavior._ast.ASTAfterStatementBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTNumericLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteralBuilder;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteralBuilder;
import de.monticore.siunits._ast.*;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehaviorBuilder;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Trafo which replaces templated ports (external inputs).
 * <p>
 * For each port which should be mocked, a new component is generated and connected to the port.
 * The newly created component then yields recorded messages.
 */

public class ExternalPortMockTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "ExternalPortMockTrafo";

    private final String mainCompName;
    private final ReplayDataHandler dataHandler;

    private final File modelPath;

    public ExternalPortMockTrafo(File modelPath, File replayDataFile, String mainComp) {
        this.mainCompName = mainComp;
        this.dataHandler = new ReplayDataHandler(replayDataFile);
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: External Input Mock: " + targetComp.getComponentType().getName(), TOOL_NAME);


        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);

        FindPortNamesVisitor visitorPortNames = new FindPortNamesVisitor();
        targetComp.accept(visitorPortNames);

        // get parent components and see if any of the incoming ports is not connected
        // if this is the case the port must be externally connected
        for (String parentName : TrafoUtil.findParents(allModels, targetComp)) {
            ASTMACompilationUnit parentComp = TrafoUtil.getComponentByName(allModels, parentName);
            List<ASTComponentInstantiation> instantiations = TrafoUtil.getInstantiationsByType(parentComp, targetComp.getComponentType().getName());

            for (ASTComponentInstantiation instantiation : instantiations) {
                // for each port check if connection is present
                for (String instanceName : instantiation.getInstancesNames()) {
                    String qNameComp = targetComp.getPackage().getQName() + "." + targetComp.getComponentType().getName();
                    // incoming ports
                    for (String portName : visitorPortNames.getIngoingPorts()) {
                        String qName = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingTarget = parentComp.getComponentType().getConnectorsMatchingTarget(qName);

                        if (connectorsMatchingTarget.size() == 0) {
                            additionalTrafoModels.add(transform(targetComp, true, qNameComp, portName));
                        }
                    }

                    // outgoing ports
                    for (String portName : visitorPortNames.getOutgoingPorts()) {
                        String qName = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingSource = parentComp.getComponentType().getConnectorsMatchingSource(qName);

                        if (connectorsMatchingSource.size() == 0) {
                            additionalTrafoModels.add(transform(targetComp, false, qNameComp, portName));
                        }
                    }
                }
            }
        }

        return additionalTrafoModels;
    }

    public ASTMACompilationUnit transform(ASTMACompilationUnit targetComp,
                                          boolean isIngoingPort,
                                          String qNameInstance,
                                          String port) throws Exception {
        // naming convention as follows <Component><Port>Mock, e.g. SourceSensorMock
        String mockedComponentName = TrafoUtil.capitalize(targetComp.getComponentType().getName()) + TrafoUtil.capitalize(port) + "Mock";

        // adds new subcomponent representing the external input
        ASTMACompilationUnit mockedPort = createCompilationUnit(targetComp.getPackage(), mockedComponentName);

        // TODO add actual behavior
        if (isIngoingPort) {
            addBehavior(mockedPort, qNameInstance, port);
        }

        // the corresponding connected mocking port has the reversed direction
        addPort(mockedPort,
                isIngoingPort ? "out" : "in",
                isIngoingPort,
                TrafoUtil.getPortTypeByName(targetComp, port));


        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(targetComp.getPackage());
        fullyQName.addParts(mockedComponentName);
        addSubComponentInstantiation(targetComp, fullyQName, mockedComponentName.toLowerCase(), createEmptyArguments());


        if (isIngoingPort) {
            addConnection(targetComp, mockedComponentName.toLowerCase() + ".out", port);
        } else {
            addConnection(targetComp, port, mockedComponentName.toLowerCase() + ".in");
        }


        return mockedPort;
    }

    void addBehavior(ASTMACompilationUnit compilationUnit, String qNameComp, String portName) {
        List<JsonObject> recordings = dataHandler.getRecordings(qNameComp, portName);

        for (JsonObject recording : recordings) {
            long timestamp = recording.getJsonNumber("timestamp").longValue();
            String value =  recording.getString("msg_content");
            addAfterBehaviorBlock(timestamp, portName, value);
        }

    }

    private void addAfterBehaviorBlock(long timestamp, String portName, String value) {
        // Build timestamp number
        ASTLongLiteralBuilder tsLiternalNumeric = new ASTLongLiteralBuilder();
        tsLiternalNumeric.setSource(String.valueOf(timestamp));
/*
        // Build timestamp unit
        ASTSIUnitBuilder timestampUnit = MontiThingsMill.sIUnitBuilder();
        ASTSIUnitPrimitiveBuilder astsiUnitPrimitive = MontiThingsMill.sIUnitPrimitiveBuilder();
        ASTSIUnitWithPrefixBuilder astsiUnitWithPrefix = MontiThingsMill.sIUnitWithPrefixBuilder();
        astsiUnitWithPrefix.set
        astsiUnitPrimitive.setSIUnitWithPrefix(astsiUnitWithPrefix)


        timestampUnit.setSIUnitPrimitive(astsiUnitPrimitive)
        // Build timestamp SI unit literal
        ASTSIUnitLiteralBuilder tsLiteral = MontiThingsMill.sIUnitLiteralBuilder();
        tsLiteral.setNumericLiteral(tsLiternalNumeric.build());
        tsLiteral.setSIUnit(timestampUnit)

        ASTMCJavaBlock javaBlock = MontiThingsMill.mCJavaBlockBuilder().build();
        ASTAfterStatementBuilder afterBehavior = MontiThingsMill.afterStatementBuilder();
        afterBehavior.setSIUnitLiteral(tsLiteral)
        afterBehavior.setMCJavaBlock(javaBlock);
        comp.getComponentType().getBody().addArcElement(behavior.build());
*/
    }
}
