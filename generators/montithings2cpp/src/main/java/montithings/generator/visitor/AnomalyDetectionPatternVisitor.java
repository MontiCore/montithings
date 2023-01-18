package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.MontiThingsTraverser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnomalyDetectionPatternVisitor implements MontiThingsTraverser {
    private boolean hasUnivariatePorts = false;
    private boolean hasMultivariatePorts = false;

    public boolean createUnivariateAnomalyDetection() {
        return hasUnivariatePorts;
    }

    public boolean createMultivariateAnomalyDetection() {
        return hasMultivariatePorts;
    }

    @Override
    public void visit(ASTMTComponentType node) {
        Preconditions.checkArgument(node != null);
        Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. " + "Did you forget to run the SymbolTableCreator?", node.getName());
        ComponentTypeSymbol compSymbol = node.getSymbol();

        Map<String, Integer> portTypeToCount = this.getPortTypeCount(compSymbol);

        for (int count : portTypeToCount.values()) {
            if (count == 1) {
                this.hasUnivariatePorts = true;
            } else if (count > 1) {
                this.hasMultivariatePorts = true;
            }
        }
    }

    private Map<String, Integer> getPortTypeCount(ComponentTypeSymbol compSymbol) {
        Map<String, Integer> portTypeToCount = new HashMap<>();

        for (PortSymbol port : compSymbol.getAllIncomingPorts()) {
            String portType = port.getTypeInfo().getName();

            if (this.isNumericPort(portType)) {
                if (portTypeToCount.containsKey(portType)) {
                    portTypeToCount.put(portType, portTypeToCount.get(portType) + 1);
                } else {
                    portTypeToCount.put(portType, 1);
                }
            }
        }

        return portTypeToCount;
    }

    private boolean isNumericPort(String portType) {
        List<String> numericPortTypes = Arrays.asList("int", "double", "float");
        return numericPortTypes.contains(portType);
    }
}
