import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ControlFlowAnalysisPhase {
    List<BasicBlock> blocks;
    BasicBlock first;
    ControlFlowAnalysisPhase(List<BasicBlock> blocks) {
        this.blocks = blocks;
        this.first = blocks.get(0);
        this.first = decompile(first);
    }

    public List<BasicBlock> getBlocks() {
        return this.blocks;
    }

    public BasicBlock decompile(BasicBlock firstBlock) {
        // while (firstBlock.getSuccessors().size() != 0) {
            Set<BasicBlock> visited = new HashSet<>();
            decompile(firstBlock, visited);
            visited = new HashSet<>();
            decompile(firstBlock, visited);
        // }

        return null;
    }

    public void decompile(BasicBlock firstBlock, Set<BasicBlock> visited) {
        if (visited.contains(firstBlock)) return;
        visited.add(firstBlock);
        if (isBackEdge(firstBlock) && firstBlock.getSuccessors().size() == 2) {
            System.out.println("IN THIS");
            BasicBlock leftBlock = firstBlock.getSuccessors().get(0);
            BasicBlock rightBlock = firstBlock.getSuccessors().get(1);
            BasicBlock trueEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.False ? leftBlock : rightBlock;
            BasicBlock falseEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.True ? leftBlock : rightBlock;

            if (falseEdge.getSuccessors().contains(firstBlock)) {
                int exprCount = firstBlock.getInstructions().size();
                Expr conditional = ((Stmt.Expression) firstBlock.getInstructionAt(exprCount - 1)).expression;
                List<Stmt> statements = new ArrayList<>();
                for (Object stmt : falseEdge.getInstructions()) {
                    Stmt statement = (Stmt) stmt;
                    statements.add(statement);
                }

                Stmt whileBody = new Stmt.Block(statements);
                Stmt.While whileStmt = new Stmt.While(conditional, whileBody);

                firstBlock.setInstructionAt(exprCount - 1, whileStmt);
                firstBlock.setLoop(false);
                List<BasicBlock> successors = new ArrayList<>();
                successors.add(trueEdge);
                
                firstBlock.setSuccessors(successors);
                blocks.remove(falseEdge);
            }
        } else if (firstBlock.getSuccessors().size() == 2) {

        } else if (firstBlock.getPredecessors().size() == 1) {
            BasicBlock prevBlock = firstBlock.getPredecessors().get(0);
            if (prevBlock.getSuccessors().size() == 1) {
                for (Object expr : firstBlock.getInstructions()) {
                    prevBlock.addInstruction(expr);
                }
                prevBlock.setSuccessors(firstBlock.getSuccessors());
                prevBlock.setEdgeType(firstBlock.getEdgeType());
                this.blocks.remove(firstBlock);
            }
        }

        for (BasicBlock child : firstBlock.getSuccessors()) {
            decompile(child, visited);
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
