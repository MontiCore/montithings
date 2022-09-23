// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2proto.CoCos;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._ast.ASTCDLeftToRightDir;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Context Condition that ensures that the associations (and the directed graph that they induce) are cycle free
 * With our current implementation cycles cannot be properly serialized (e.g. the message would be infinite)
 */
public class NoCircleCoCo implements CDBasisASTCDDefinitionCoCo {
    Graph graph = new Graph();

    @Override
    public void check(ASTCDDefinition node) {
        List<ASTCDAssociation> associations = node.getCDAssociationsList();

        buildGraph(associations);

        if(graph.hasCycle()) Log.error("The provided Classdiagram's associations contain a cycle! - Only DAGs are supported!");
    }

    /**
     * @param associations Takes list of associations and builds a directed graph from them (look at Vertex/Graph classes
     *                     for more information
     */
    private void buildGraph(List<ASTCDAssociation> associations) {
        for(ASTCDAssociation association : associations) {
            if(association.getCDAssocDir().isBidirectional()) {
                Log.error("Classdiagram contains a bidirectional association and therefore is not acyclic! - Only DAGs are supported!");
            }
            else {
                Vertex left  = new Vertex(association.getLeftQualifiedName().toString());
                Vertex right = new Vertex(association.getRightQualifiedName().toString());

                if(!graph.addVertex(left)) {
                    left = graph.findVertex(left);
                }
                if(!graph.addVertex(right)) {
                    right = graph.findVertex(right);
                }

                if(association.getCDAssocDir() instanceof ASTCDLeftToRightDir) graph.addEdge(left, right);
                else graph.addEdge(right, left);
            }
        }
    }
}