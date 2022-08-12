package montithings.generator.cd2proto.CoCos;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class together with the Vertex class builds the infrastructure to build a graph,
 * which is then used to check whether associations inside a Classdiagram are acyclic.
 * Inspiration for the algorithm and corresponding Graph and Vertex classes is
 * taken from https://www.baeldung.com/java-graph-has-a-cycle - last visited: 12.08.2022
 */
public class Graph {

    final private List<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    /**
     * @param vertex Vertex that should be added to the graph
     * @return returns a boolean value, true = vertex was added, false = vertex already existed and therefore was not added again
     *
     * This method takes a vertex and adds it to the graph, if it does not exist yet.
     * Returning a boolean value is necessary, since the passed vertex and the potentially already contained vertex
     * are supposed to be the same, but are two different objects, and we need to know if we have to look up the correct
     * vertex in the next step.
     */
    public boolean addVertex(Vertex vertex) {
        if(!vertices.contains(vertex)) {
            this.vertices.add(vertex);
            return true;
        }
        else return false;
    }

    /**
     * @param vertex Vertex that has to be looked up
     * @return If the vertex exists, the correct vertex (existing one with the same label) is returned, otherwise the passed vertex is returned
     */
    public Vertex findVertex(Vertex vertex) {
        for(Vertex vertex_local : vertices) {
            if (vertex_local.getLabel().equals(vertex.getLabel())) return vertex_local;
        }
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to) {
        from.addNeighbor(to);
    }

    /**
     * @param sourceVertex Initial vertex
     * @return Value that contains the information whether the graph contains a cycle
     *
     * Algorithm to determine if (a component of) a graph contains a cycle.
     * Start with a vertex, then perform a search outgoing from that vertex marking all visited vertices
     * If we visit a vertex twice the graph must contain a cycle.
     */
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

    /**
     * @return Value that contains the information whether the graph contains a cycle
     *
     * Since our graphs can have multiple components, we need to iterate over all vertices/components,
     * thus needing an additional method for that purpose that calls the actual algorithm
     */
    public boolean hasCycle() {
        for (Vertex vertex : vertices) {
            if (!vertex.isVisited() && hasCycle(vertex)) {
                return true;
            }
        }
        return false;
    }
}
