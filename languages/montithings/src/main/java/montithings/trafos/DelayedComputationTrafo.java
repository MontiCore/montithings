// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTArcParameter;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesNodeFactory;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transformer for delaying computation times according to the recorded data.
 * <p>
 * This is done as follows:
 * The original component is moved into a new wrapping component where two subcomponents are interposed before and
 * after the ports of the component. When a message is received, a timestamp is calculated and sent to the second subcomponent.
 * The values themselves are just forwarded. The second subcomponent is aware of how long the computation should take
 * and adds delay in case that the computation is completed too fast.
 * <p>
 * This transformation moves the original component into a wrapping one, creates the new subcomponents and
 * rewires its connections.
 */

public class DelayedComputationTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "ExternalPortMock";

    private final ReplayDataHandler dataHandler;

    public DelayedComputationTrafo(File replayDataFile) {
        this.dataHandler = new ReplayDataHandler(replayDataFile);
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: Delayed Computation: " + targetComp.getComponentType().getName(), TOOL_NAME);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        // retrieves a list of all ports of the original component
        FindPortNamesVisitor visitor = new FindPortNamesVisitor();
        targetComp.accept(visitor);
        Set<String> origIngoingPorts = visitor.getIngoingPorts();
        Set<String> origOutgoingPorts = visitor.getOutgoingPorts();

        if (origIngoingPorts.size() == 0 ||
                origOutgoingPorts.size() == 0) {
            // No need to include computation delay
            return additionalTrafoModels;
        }

        /* ============================================================ */
        /* ======================= COMPONENT CREATION ================= */
        /* ============================================================ */

        // creates a wrapping component, e.g. SinkWrapper for component Sink
        String compName = targetComp.getComponentType().getName();
        String compWrapperName = compName + "Wrapper";
        ASTMACompilationUnit compWrapper = createCompilationUnit(targetComp.getPackage(), compWrapperName);

        // copies parameters from original model
        List<ASTArcParameter> parameterList = targetComp.getComponentType().getHead().getArcParameterList();
        compWrapper.getComponentType().getHead().setArcParameterList(parameterList);

        // For each wrapping component, two additional components are required: before and after the computation
        ASTMACompilationUnit[] newComps = new ASTMACompilationUnit[2];
        String[] newCompNames = {
                compWrapperName + "ComputationStart", compWrapperName + "ComputationEnd"
        };

        newComps[0] = createCompilationUnit(targetComp.getPackage(), compWrapperName + "ComputationStart");
        newComps[1] = createCompilationUnit(targetComp.getPackage(), compWrapperName + "ComputationEnd");

        addBehaviorStartComputation(newComps[0]);
        addBehaviorEndComputation(newComps[1]);

        additionalTrafoModels.add(compWrapper);
        additionalTrafoModels.add(newComps[0]);
        additionalTrafoModels.add(newComps[1]);

        /* ============================================================ */
        /* ======================= INSTANTIATIONS ===================== */
        /* ============================================================ */
        // In case of parameterized components the wrapping component takes the same parameters as the original one and simply forwards it
        // e.g. component LowPassFilterWrapper (int threshold, int defaultValue) {
        //          LowPassFilter lpf (threshold, defaultValue);
        //      }
        List<String> parameterStringList = parameterList.stream()
                .map(ASTArcParameter::getName)
                .collect(Collectors.toList());
        ASTArguments arguments = createArguments(parameterStringList);

        addSubComponentInstantiation(compWrapper, targetComp.getPackage(), compName.toLowerCase(), arguments);

        ASTMCQualifiedName fullyQName1 = copyASTMCQualifiedName(targetComp.getPackage());
        fullyQName1.addParts(newCompNames[0]);
        addSubComponentInstantiation(compWrapper, fullyQName1, newCompNames[0].toLowerCase(), createEmptyArguments());

        ASTMCQualifiedName fullyQName2 = copyASTMCQualifiedName(targetComp.getPackage());
        fullyQName2.addParts(newCompNames[1]);
        addSubComponentInstantiation(compWrapper, fullyQName2, newCompNames[1].toLowerCase(), createEmptyArguments());

        // wherever the original component was initiated, the declaration has to be changed.
        // E.g. "Sink sink" becomes "SinkWrapper sink"
        for (String parent : TrafoUtil.findParents(originalModels, targetComp)) {
            ASTMACompilationUnit p = TrafoUtil.getComponentByName(originalModels, parent);
            replaceComponentInstantiationType(p, compName, compWrapperName);

        }

        /* ============================================================ */
        /* ======================= PORT CREATION ====================== */
        /* ============================================================ */
        // the wrapper component requires the same ports
        // hence, mirror the port declarations
        for (String port : origIngoingPorts) {
            addPort(compWrapper, port, false,
                    TrafoUtil.getPortTypeByName(targetComp, port));
        }

        for (String port : origOutgoingPorts) {
            addPort(compWrapper, port, true,
                    TrafoUtil.getPortTypeByName(targetComp, port));
        }


        // add ports for the ComputationStart|End components
        // creates forwarding ports for each port of the original component
        for (String origIngoingPort : origIngoingPorts) {
            addPort(newComps[0],
                    origIngoingPort + "_in",
                    false,
                    TrafoUtil.getPortTypeByName(targetComp, origIngoingPort));
            addPort(newComps[0],
                    origIngoingPort + "_out",
                    true,
                    TrafoUtil.getPortTypeByName(targetComp, origIngoingPort));
        }

        for (String origOutgoingPort : origOutgoingPorts) {
            addPort(newComps[1],
                    origOutgoingPort + "_in",
                    false,
                    TrafoUtil.getPortTypeByName(targetComp, origOutgoingPort));
            addPort(newComps[1],
                    origOutgoingPort + "_out",
                    true,
                    TrafoUtil.getPortTypeByName(targetComp, origOutgoingPort));
        }

        // adds additional port for both subcomponents in order to exchange timestamps
        //TODO: is there a better method of getting a primitive type of Long?
        ASTMCPrimitiveType longPrimitiveType = MCBasicTypesNodeFactory.createASTMCPrimitiveType();
        longPrimitiveType.setPrimitive(7);

        // port name is prefixed by "rnr" (record and replay) in order to avoid name clashes
        addPort(newComps[0],
                "rnr_timestamp",
                true,
                longPrimitiveType
        );
        addPort(newComps[1],
                "rnr_timestamp",
                false,
                longPrimitiveType);

        /* ============================================================ */
        /* ======================= CONNECTION CREATION ================ */
        /* ============================================================ */

        // adds new connections
        // connects connections between original and computationStart|End
        origIngoingPorts.forEach(port -> {
            addConnection(compWrapper,
                    port,
                    newCompNames[0].toLowerCase() + "." + port + "_in");
            addConnection(compWrapper,
                    newCompNames[0].toLowerCase() + "." + port + "_out",
                    compName.toLowerCase() + "." + port);
        });

        origOutgoingPorts.forEach(port -> {
            addConnection(compWrapper,
                    compName.toLowerCase() + "." + port,
                    newCompNames[1].toLowerCase() + "." + port + "_in");
            addConnection(compWrapper,
                    newCompNames[1].toLowerCase() + "." + port + "_out",
                    port);
        });

        // exchange of timestamps
        addConnection(compWrapper,
                newCompNames[0].toLowerCase() + ".rnr_timestamp",
                newCompNames[1].toLowerCase() + ".rnr_timestamp");

        return additionalTrafoModels;
    }

    private void addBehaviorStartComputation(ASTMACompilationUnit comp) {
        // TODO
        addEmptyBehavior(comp);

    }


    private void addBehaviorEndComputation(ASTMACompilationUnit comp) {
        // TODO
        addEmptyBehavior(comp);
    }
}
