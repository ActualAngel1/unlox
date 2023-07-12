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
            visited.clear();
            clean(firstBlock, visited);

            visited.clear();
            decompile(firstBlock, visited);

            visited.clear();
            clean(firstBlock, visited);

            visited.clear();
            decompile(firstBlock, visited);

            visited.clear();
            clean(firstBlock, visited);

            visited.clear();
            decompile(firstBlock, visited);

            visited.clear();
            clean(firstBlock, visited);

            visited.clear();
            decompile(firstBlock, visited);


        // }

        return null;
    }

    public void decompile(BasicBlock firstBlock, Set<BasicBlock> visited) {
        if (visited.contains(firstBlock)) return;
        visited.add(firstBlock);
        if (isBackEdge(firstBlock) && firstBlock.getSuccessors().size() == 2) {
            BasicBlock leftBlock = firstBlock.getSuccessors().get(0);
            BasicBlock rightBlock = firstBlock.getSuccessors().get(1);
            BasicBlock trueEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.False ? leftBlock : rightBlock;
            BasicBlock falseEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.True ? leftBlock : rightBlock;

            if (falseEdge.getSuccessors().contains(firstBlock)) {
                System.out.println("IN THIS");
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
            BasicBlock leftBlock = firstBlock.getSuccessors().get(0);
            BasicBlock rightBlock = firstBlock.getSuccessors().get(1);
            BasicBlock falseEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.False ? leftBlock : rightBlock;
            BasicBlock trueEdge = leftBlock.getEdgeType() == BasicBlock.EdgeType.True ? leftBlock : rightBlock;

            System.out.println("Is false block empty? : " + blocks.indexOf(falseEdge) + " " + isEmptyBlock(falseEdge));
            if (isEmptyBlock(falseEdge) && falseEdge.getSuccessorAt(0) == trueEdge.getSuccessorAt(0)) {
                int exprCount = firstBlock.getInstructions().size();
                Expr condition = ((Stmt.Expression) firstBlock.getInstructionAt(exprCount - 1)).expression;
                List<Stmt> statements = new ArrayList<>();
                for (Object stmt : trueEdge.getInstructions()) {
                    Stmt statement = (Stmt) stmt;
                    statements.add(statement);
                }

                Stmt ifBody = new Stmt.Block(statements);
                Stmt.If ifStmt = new Stmt.If(condition, ifBody, null);
                firstBlock.setInstructionAt(exprCount - 1, ifStmt);
                BasicBlock grandson = trueEdge.getSuccessorAt(0);
                List<BasicBlock> successors = new ArrayList<>();
                successors.add(grandson);

                firstBlock.setSuccessors(successors);
                firstBlock.setEdgeType(grandson.getEdgeType());
                blocks.remove(trueEdge);
                blocks.remove(falseEdge);
            }
        } else if (firstBlock.getSuccessors().size() == 1) {
            BasicBlock nextBlock = firstBlock.getSuccessorAt(0);
            if ((nextBlock.getPredecessors().size() == 1 || nextBlock.getPredecessors().size() == 0) && !nextBlock.isLoop()) {
                for (Object expr : nextBlock.getInstructions()) {
                    firstBlock.addInstruction(expr);
                }

                firstBlock.setSuccessors(nextBlock.getSuccessors());
                this.blocks.remove(nextBlock);
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

    // When classifying the control flow into statements there could be cases where the predecessor or successors list has empty items.
    public void clean(BasicBlock block, Set<BasicBlock> visited) {
        if (visited.contains(block)) return;
        visited.add(block);
        for (int i = 0; i < block.getPredecessors().size(); i++) {
            BasicBlock pred =  block.getPredecessors().get(i);
            if (!blocks.contains(pred) || pred == null) {
                blocks.remove(pred); // Unnecessary
                block.deletePredecessor(pred);
            }
        }

        for (int i = 0; i < block.getSuccessors().size(); i++) {
            BasicBlock succ =  block.getSuccessorAt(i);
            if (!blocks.contains(succ) || succ == null) {
                blocks.remove(succ);
                block.deleteSuccessor(succ);
            }
        }

        for (BasicBlock child : block.getSuccessors()) {
            clean(child, visited);
        }
    }

    public boolean isEmptyBlock(BasicBlock block) {
        if (block.getInstructions().size() == 0) return true;
        if (block.getInstructionAt(0) == null) return true;

        for (Object object : block.getInstructions()) {
            if (!(object == null)) return false;
        }

        return true;
    }
}
