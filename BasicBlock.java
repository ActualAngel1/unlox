import java.util.ArrayList;
import java.util.List;

public class BasicBlock {

    private List<Instruction> Block;
    private List<BasicBlock> children;
    private Instruction jump;

    BasicBlock(List<Instruction> Block, List<BasicBlock> children, Instruction jump) {
        this.Block = Block;
        this.children = children;
        this.jump = jump;
    }

    BasicBlock(List<Instruction> Block, Instruction jump) {
        this.Block = Block;
        this.children = new ArrayList<>();
        this.jump = jump;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.Block = instructions;
    }

    public List<Instruction> getInstructions() {
        return this.Block;
    }

    public void addChild(BasicBlock block) {
        this.children.add(block);
    }
    public void setJump(Instruction jump) {
        this.jump = jump;
    }

    public Instruction getJump() {
        return this.jump;
    }

    public void setChildren(List<BasicBlock> children) {
        this.children = children;
    }

    public BasicBlock getChildAt(int index) {
        return this.children.get(index);
    }

    public List<BasicBlock> getChildren() {
        return this.children;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("\n Basic block: \n");
        for (Instruction instruction : Block) {
            str.append(instruction.toString()).append('\n');
        }

        return str.toString();
    }
}
