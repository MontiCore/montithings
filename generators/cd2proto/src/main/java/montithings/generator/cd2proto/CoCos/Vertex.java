// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2proto.CoCos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This Class together with the Graph class builds the infrastructure to build a graph,
 * which is then used to check whether associations inside a Classdiagram are acyclic.
 * Inspiration for the algorithm and corresponding Graph and Vertex classes is
 * taken from https://www.baeldung.com/java-graph-has-a-cycle - last visited: 12.08.2022
 */
public class Vertex {
    final private String label;
    private boolean beingVisited;
    private boolean visited;
    final private List<Vertex> adj;

    public Vertex(String label) {
        this.label = label;
        this.adj = new ArrayList<>();
    }
    public void addNeighbor(Vertex adjacent) {
        this.adj.add(adjacent);
    }

    public void setBeingVisited(boolean bool) {
        this.beingVisited = bool;
    }

    public List<Vertex> getAdjacencyList() {
        return this.adj;
    }

    public void setVisited(boolean bool) {
        this.visited = bool;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public boolean isBeingVisited() {
        return this.beingVisited;
    }

    public String getLabel() { return this.label; }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Vertex)) return false;

        return ((Vertex) obj).label.equals(this.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, beingVisited, visited, adj);
    }
}
