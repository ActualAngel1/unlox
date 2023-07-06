import java.util.ArrayList;
import java.util.List;

public class BasicBlock {

    private final List<Instruction> Block;
    private List<BasicBlock> children;
    private final Instruction jump;

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

    public void addChild(BasicBlock block) {
        this.children.add(block);
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
