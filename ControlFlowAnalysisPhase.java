import java.util.ArrayList;
import java.util.List;

public class ControlFlowAnalysisPhase {
    List<BasicBlock> blocks;
    BasicBlock firstBlock;
    ControlFlowAnalysisPhase(List<BasicBlock> blocks) {
        this.blocks = blocks;
        this.firstBlock = blocks.get(0);
    }

    public BasicBlock decompile() {
        return null;
    }

    public boolean isLoop(BasicBlock block) {
        int blockId = block.getId();
        List<Integer> visitedIds = new ArrayList<>();
        for (BasicBlock child : block.getSuccessors()) {

        }
    }
}
