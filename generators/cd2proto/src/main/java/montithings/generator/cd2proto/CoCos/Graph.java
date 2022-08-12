/*
 * This Class together with the Vertex class builds the infrastructure to build a graph,
 * which is then used to check whether associations inside a Classdiagram are acyclic.
 * Inspiration for the algorithm and corresponding Graph and Vertex classes is
 * taken from https://www.baeldung.com/java-graph-has-a-cycle - last visited: 12.08.2022
 */

package montithings.generator.cd2proto.CoCos;

import java.util.ArrayList;
import java.util.List;
public class Graph {

    final private List<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public boolean addVertex(Vertex vertex) {
        if(!vertices.contains(vertex)) {
            this.vertices.add(vertex);
            return true;
        }
        else return false;
    }

    public Vertex findVertex(Vertex vertex) {
        for(Vertex vertex_local : vertices) {
            if (vertex_local.getLabel().equals(vertex.getLabel())) return vertex_local;
        }
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to) {
        from.addNeighbor(to);
    }

    public boolean hasCycle(Vertex sourceVertex) {
        sourceVertex.setBeingVisited(true);

        for (Vertex neighbor : sourceVertex.getAdjacencyList()) {
            if (neighbor.isBeingVisited()) {
                return true;
            } else if (!neighbor.isVisited() && hasCycle(neighbor)) {
                return true;
            }
        }

        sourceVertex.setBeingVisited(false);
        sourceVertex.setVisited(true);
        return false;
    }

    public boolean hasCycle() {
        for (Vertex vertex : vertices) {
            if (!vertex.isVisited() && hasCycle(vertex)) {
                return true;
            }
        }
        return false;
    }
}
