import java.util.ArrayList;
import java.util.List;

public class Node {
    private List<Node> successors = new ArrayList<>();
    private List<Node> predecessors = new ArrayList<>();
    private List<Node> dominators = new ArrayList<>();
    private Node immediateDominator;

    public List<Node> getSuccessors() {
        return this.successors;
    }

    public List<Node> getPredecessors() {
        return this.predecessors;
    }

    public void setSuccessors(List<Node> successors) {
        this.successors = successors;
    }

    public void setPredecessors(List<Node> predecessors) {
        this.predecessors = predecessors;
    }

    public List<Node> getDominators() {
        return dominators;
    }

    public void setDominators(List<Node> dominators) {
        this.dominators = dominators;
    }

    public Node getImmediateDominator() {
        return this.immediateDominator;
    }

    public void setImmediateDominator(Node immediateDominator) {
        this.immediateDominator = immediateDominator;
    }
}
