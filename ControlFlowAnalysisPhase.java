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
            Set<Integer> visited = new HashSet<>();
            decompile(firstBlock, visited);
        }

        return null;
    }

    public void decompile(BasicBlock firstBlock, Set<Integer> visited) {
        BasicBlock newBlock;
        if (visited.contains(firstBlock.getId())) return;
        if (isBackEdge(firstBlock)) {

        }
        for (BasicBlock child : firstBlock.getSuccessors()) {
            decompile(child);
        }
    }

    public boolean isBackEdge (BasicBlock child) {
        return child.isLoop();
    }

    public boolean isInLoop(BasicBlock block) {
        int blockId = block.getId();
        Set<Integer> visitedIds = new HashSet<>();
        return isInLoop(block, visitedIds, blockId);
    }

    public boolean isInLoop(BasicBlock block, Set<Integer> visited, int blockId) {
        for (BasicBlock child : block.getSuccessors()) {
            if (child.getId() == blockId) return true;
            if (visited.contains(child.getId())) continue;
            visited.add(child.getId());
            isInLoop(child, visited, blockId);
        }

        return false;
    }
}
