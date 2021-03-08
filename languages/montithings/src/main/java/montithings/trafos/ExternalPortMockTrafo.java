// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentInstantiation;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static montithings.util.GenericBindingUtil.printSimpleType;

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

    public ExternalPortMockTrafo(File replayDataFile, String mainComp) {
        this.mainCompName = mainComp;
        this.dataHandler = new ReplayDataHandler(replayDataFile);
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: External Input Mock: " + targetComp.getComponentType().getName(), TOOL_NAME);


        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);

        // external inputs are only defined in the outermost component (or its subcomponents)
        ASTMACompilationUnit mainComp = TrafoUtil.getComponentByName(originalModels, targetComp, this.mainCompName);

        // trafos are applied on all original models. this time, however, it should only be applied once
        if (!mainComp.equals(targetComp)) {
            // only apply trafo once
            return additionalTrafoModels;
        }

        FindConnectionsVisitor visitorConn = new FindConnectionsVisitor();
        mainComp.accept(visitorConn);
        List<FindConnectionsVisitor.Connection> connections = visitorConn.getConnections();

        /* ============================================================ */
        /* ======================= MAINCOMPONENT PORTS ================ */
        /* ============================================================ */

        //TODO: not implemented yet, is this even possible?

        /* ============================================================ */
        /* ======================= SUBCOMPONENT PORTS ================= */
        /* ============================================================ */

        // in order to find external ports we have to iterate over all subcomponents and
        // check if their incoming ports are connected somehow
        for (ASTComponentInstantiation instantiation : mainComp.getComponentType().getSubComponentInstantiations()) {
            for (String instancesName : instantiation.getInstancesNames()) {
                String type = printSimpleType(instantiation.getMCType());
                String subCompQName = mainComp.getPackage().getQName() + "." + type;

                ASTMACompilationUnit subComp = TrafoUtil.getComponentByName(allModels, mainComp, subCompQName);

                FindPortNamesVisitor visitorPortNames = new FindPortNamesVisitor();
                subComp.accept(visitorPortNames);

                for (String portName : visitorPortNames.getIngoingPorts()) {
                    String qName = instancesName + "." + portName;
                    boolean isPortConnected = connections.stream()
                            .anyMatch(c -> c.target.getQName().equals(qName));
                    if (!isPortConnected) {
                        additionalTrafoModels.add(transform(mainComp, subComp, true, portName));
                    }
                }

                for (String portName : visitorPortNames.getOutgoingPorts()) {
                    String qName = instancesName + "." + portName;
                    boolean isPortConnected = connections.stream()
                            .anyMatch(c -> c.source.getQName().equals(qName));
                    if (!isPortConnected) {
                        additionalTrafoModels.add(transform(mainComp, subComp, false, portName));
                    }
                }
            }
        }

        return additionalTrafoModels;
    }

    public ASTMACompilationUnit transform(ASTMACompilationUnit mainComp,
                                          ASTMACompilationUnit comp,
                                          boolean isIngoingPort,
                                          String port) throws Exception {
        // naming convention as follows <Component><Port>Mock, e.g. SourceSensorMock
        String mockedComponentName = TrafoUtil.capitalize(comp.getComponentType().getName()) + TrafoUtil.capitalize(port) + "Mock";

        // adds new subcomponent representing the external input
        ASTMACompilationUnit mockedPort = createCompilationUnit(comp.getPackage(), mockedComponentName);

        // TODO add actual behavior
        addEmptyBehavior(mockedPort);

        // the corresponding connected mocking port has the reversed direction
        addPort(mockedPort,
                isIngoingPort ? "out" : "in",
                isIngoingPort,
                TrafoUtil.getPortTypeByName(comp, port));

        // find out the instance name
        List<String> instanceNames = mainComp.getComponentType().getSubComponentInstantiations().stream()
                .filter(s -> comp.getComponentType().getName().equals(printSimpleType(s.getMCType())))
                .map(ASTComponentInstantiation::getInstancesNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        addSubComponentInstantiation(mainComp, mockedComponentName, mockedComponentName.toLowerCase(), createEmptyArguments());

        // connects mocked component with templated port
        for (String instanceName : instanceNames) {
            if (isIngoingPort) {
                addConnection(mainComp, mockedComponentName.toLowerCase() + ".out", instanceName + "." + port);
            } else {
                addConnection(mainComp, instanceName + "." + port, mockedComponentName.toLowerCase() + ".in");
            }
        }

        return mockedPort;
    }
}
