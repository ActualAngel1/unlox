import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private List<Instruction> Block;
    private List<BasicBlock> successors;
    private Instruction jump;

    BasicBlock(List<Instruction> Block, List<BasicBlock> successors, Instruction jump) {
        this.Block = Block;
        this.successors = successors;
        this.jump = jump;
    }

    BasicBlock(List<Instruction> Block, Instruction jump) {
        this.Block = Block;
        this.successors = new ArrayList<>();
        this.jump = jump;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.Block = instructions;
    }

    public List<Instruction> getInstructions() {
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
        for (Instruction instruction : Block) {
            str.append(instruction.toString()).append('\n');
        }

        return str.toString();
    }
}
