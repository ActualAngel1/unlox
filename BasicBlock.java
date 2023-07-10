import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    enum EdgeType {
        False, True
    }
    private EdgeType type;
    private boolean isLoop = false;
    private static int id = 0;
    private List<Object> Block;
    private List<BasicBlock> successors;
    private List<BasicBlock> predecessors = new ArrayList<>();
    private Instruction jump;

    BasicBlock(List<Object> Block, List<BasicBlock> successors, Instruction jump) {
        id++;
        this.Block = Block;
        this.successors = successors;
        this.jump = jump;
        this.predecessors = new ArrayList<>();
    }

    BasicBlock(List<Object> Block, Instruction jump) {
        id++;
        this.Block = Block;
        this.successors = new ArrayList<>();
        this.jump = jump;
    }


    public void setInstructions(List<Object> instructions) {
        this.Block = instructions;
    }

    public List<Object> getInstructions() {
        return this.Block;
    }

    public void addChild(BasicBlock block) {
        this.successors.add(block);
    }
    public void setJump(Instruction jump) {
        this.jump = jump;
    }

    public Instruction getJump() {
        return this.jump;
    }

    public void setSuccessors(List<BasicBlock> children) {
        this.successors = children;
    }

    public BasicBlock getSuccessorAt(int index) {
        return this.successors.get(index);
    }

    public List<BasicBlock> getSuccessors() {
        return this.successors;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("\n Basic block: \n");
        for (Object instruction : Block) {
            if (instruction instanceof String string) {
                str.append(string);
                continue;
            }
            // TODO: I CAN GET AWAY WITH A SIMPLE TOSTRING HERE NO?

            Instruction inst = (Instruction) instruction;
            str.append(instruction.toString()).append('\n');
        }

        return str.append("Type: ")
                .append(this.type != null ? this.type.toString() : "")
                .toString();
    }

    public List<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<BasicBlock> predecessors) {
        this.predecessors = predecessors;
    }

    public void addPredecessor(BasicBlock predecessor) {
        this.predecessors.add(predecessor);
    }

    public void setEdgeType(EdgeType type) {
        this.type = type;
    }

    public EdgeType getEdgeType() {
        return this.type;
    }

    public int getId() {
        return id;
    }

    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public boolean isLoop() {
        return this.isLoop;
    }

}
