// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds all connections of a component
 */
public class FindConnectionsVisitor implements MontiThingsVisitor {
    public static class Connection {
        public final ASTPortAccess source;
        public final ASTPortAccess target;

        public Connection(ASTPortAccess source, ASTPortAccess target) {
            this.source = source;
            this.target = target;
        }
    }

    protected List<Connection> connections = new ArrayList<>();

    @Override
    public void visit(ASTConnector node) {
        Preconditions.checkArgument(node != null);

        for (ASTPortAccess target : node.getTargetList()) {
            connections.add(new Connection(node.getSource(), target));
        }
    }

    public FindConnectionsVisitor() {
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }
}
