import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFlowAnalysisPhase {
    List<BasicBlock> blocks;
    BasicBlock firstBlock;
    ControlFlowAnalysisPhase(List<BasicBlock> blocks) {
        this.blocks = blocks;
        this.firstBlock = blocks.get(0);
        this.firstBlock = decompile(firstBlock);
    }

    public BasicBlock decompile(BasicBlock firstBlock) {
        while (firstBlock.getSuccessors().size() != 0) {
            BasicBlock newBlock;
            for (BasicBlock child : firstBlock.getSuccessors()) {

                decompile(child);
            }
        }

        return null;
    }

    public boolean isLoop(BasicBlock block) {
        int blockId = block.getId();
        Set<Integer> visitedIds = new HashSet<>();
        return isLoop(block, visitedIds, blockId);
    }

    public boolean isLoop(BasicBlock block, Set<Integer> visited, int blockId) {
        for (BasicBlock child : block.getSuccessors()) {
            if (child.getId() == blockId) return true;
            if (visited.contains(child.getId())) continue;
            visited.add(child.getId());
            isLoop(child, visited, blockId);
        }

        return false;
    }
}
