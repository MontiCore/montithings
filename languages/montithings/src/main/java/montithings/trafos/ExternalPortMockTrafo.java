// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTConnector;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

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
                    // incoming ports
                    for (String portName : visitorPortNames.getIngoingPorts()) {
                        String qName = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingTarget = parentComp.getComponentType().getConnectorsMatchingTarget(qName);

                        if (connectorsMatchingTarget.size() == 0) {
                            additionalTrafoModels.add(transform(targetComp, true, portName));
                        }
                    }

                    // outgoing ports
                    for (String portName : visitorPortNames.getOutgoingPorts()) {
                        String qName = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingTarget = parentComp.getComponentType().getConnectorsMatchingSource(qName);

                        if (connectorsMatchingTarget.size() == 0) {
                            additionalTrafoModels.add(transform(targetComp, false, portName));
                        }
                    }
                }
            }
        }

        return additionalTrafoModels;
    }

    public ASTMACompilationUnit transform(ASTMACompilationUnit targetComp,
                                          boolean isIngoingPort,
                                          String port) throws Exception {
        // naming convention as follows <Component><Port>Mock, e.g. SourceSensorMock
        String mockedComponentName = TrafoUtil.capitalize(targetComp.getComponentType().getName()) + TrafoUtil.capitalize(port) + "Mock";

        // adds new subcomponent representing the external input
        ASTMACompilationUnit mockedPort = createCompilationUnit(targetComp.getPackage(), mockedComponentName);

        // TODO add actual behavior
        addEmptyBehavior(mockedPort);

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
}
