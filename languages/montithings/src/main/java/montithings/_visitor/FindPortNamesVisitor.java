// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;

import java.util.HashSet;
import java.util.Set;

public class FindPortNamesVisitor implements MontiThingsVisitor {

    protected Set<String> ingoingPorts = new HashSet<>();
    protected Set<String> outgoingPorts = new HashSet<>();

    @Override
    public void visit(ASTPortDeclaration node) {
        Preconditions.checkArgument(node != null);

        for (ASTPort astPort : node.getPortList()) {
            if (node.isIncoming()) {
                ingoingPorts.add(astPort.getName());//qName);
            } else {
                outgoingPorts.add(astPort.getName());//qName);
            }
        }

    }

    public Set<String> getIngoingPorts() {
        return ingoingPorts;
    }

    public Set<String> getOutgoingPorts() {
        return outgoingPorts;
    }
}
