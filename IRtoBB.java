import java.util.*;

public class IRtoBB {
    private final List<Instruction> IR;
    private final List<BasicBlock> blocks = new ArrayList<>();

    // Maps all instructions to their basic block
    private final Map<Integer, BasicBlock> offsetToBlock = new HashMap<>();

    IRtoBB(List<Instruction> IR) {
        this.IR = IR;
    }

    public List<BasicBlock> getBlocks() {
        return this.blocks;
    }

    public BasicBlock getCFG() {
        return this.blocks.get(0);
    }

    public void transform() {
        List<Object> block = new ArrayList<>();
        Stack<Instruction> stack = new Stack<>();

        for (Instruction current : IR) {
            stack.push(current);

            if (isInstructionControlFlowAltering(current)) {
                block.addAll(stack);
                BasicBlock currBlock = new BasicBlock(block, current);

                if (!blocks.isEmpty()) {
                    BasicBlock prevBlock = blocks.get(blocks.size() - 1);
                    linkPrevBlock(currBlock, prevBlock);
                }

                blocks.add(currBlock);
                map(block, currBlock);

                block = new ArrayList<>();
                stack = new Stack<>();
            }
        }
    }

    private boolean isControlFlowAltering(Instruction instruction) {
        return (instruction.type == OpCode.OP_JUMP ||
                instruction.type == OpCode.OP_JUMP_IF_FALSE ||
                instruction.type == OpCode.OP_LOOP);
    }

    private boolean isInstructionControlFlowAltering(Instruction instruction) {
        return (instruction.type == OpCode.OP_JUMP ||
                instruction.type == OpCode.OP_JUMP_IF_FALSE ||
                instruction.type == OpCode.OP_LOOP ||
                instruction.type == OpCode.OP_RETURN); // The fuck?
    }

    private void linkPrevBlock(BasicBlock current, BasicBlock previous) {
        /*
                note: I need to link the following block if it is a jump if false, explanation:
                if it is true, you don't jump, thus you continue to the next block, which needs to be
                linked
        */
        if (previous.getJump().type == OpCode.OP_JUMP_IF_FALSE)
                current.setEdgeType(BasicBlock.EdgeType.True);

        if (!isJumpInstruction(previous.getJump())) {
            // link the prev block to current block

            previous.addChild(current);
            current.addPredecessor(previous);
        }
    }

    private void map(List<Object> block, BasicBlock blockPointer) {
        for (Object instruction : block) {
            Instruction inst = (Instruction) instruction;
            offsetToBlock.put(inst.offset, blockPointer);
        }
    }

    public void printBlocks() {
        System.out.println("\n");
        for (int i = 0; i < blocks.size(); i++) {
            System.out.println("id: " + i + " Instructions: " + blocks.get(i));
            System.out.println("Successors: ");
            System.out.println();

            for (BasicBlock block : blocks.get(i).getSuccessors()) {
                System.out.println(blocks.indexOf(block) + ", ");
            }

            System.out.println("Predecessors: ");
            System.out.println();
            for (BasicBlock block : blocks.get(i).getPredecessors()) {
                System.out.println(blocks.indexOf(block) + ", ");
            }

            System.out.println("Pred count:" + blocks.get(i).getPredecessors().size() + " ,... " + blocks.get(i).getSuccessors().size());
        }
    }

    private boolean isJumpInstruction(Instruction instruction) {
        return (instruction.type == OpCode.OP_JUMP ||
                instruction.type == OpCode.OP_LOOP);
    }

    public void split() {
        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            Instruction lastInstruction = block.getJump();

            if (isControlFlowAltering(lastInstruction)) {
                int offset = Integer.parseInt(lastInstruction.literal);
                split(offset);
            }
         }
    }

    private BasicBlock getBlockFromOffset(int offset) {
        while (!offsetToBlock.containsKey(offset)) offset++;
        return offsetToBlock.get(offset);
    }

    private int getOffset(int offset) {
        while (!offsetToBlock.containsKey(offset)) offset++;
        offset++;
        while (!offsetToBlock.containsKey(offset)) offset++;
        return offset;
    }

    private void split(int offset) {
        // Step 1: get block
        BasicBlock block = getBlockFromOffset(offset);
        List<Object> subBlock = new ArrayList<>();
        List<Object> prevBlock = new ArrayList<>();

        if (offset == ((Instruction) block.getInstructions().get(0)).offset) return;

        // Step 2: create the new block
        for (Object instruction : block.getInstructions()) {
            Instruction inst = (Instruction) instruction;
            // TODO: GET THE RIGHT OFFSET
            if (inst.offset >= offset) {
                subBlock.add(inst);
            } else {
                prevBlock.add(inst);
            }
        }

        if (prevBlock.isEmpty()) return;

        // Step 3: Link and add the new block to the block list
        BasicBlock newBlock = new BasicBlock(subBlock, block.getJump());
        List<BasicBlock> prevEdges = block.getSuccessors();
        newBlock.setSuccessors(prevEdges);
        block.setSuccessors(new ArrayList<>());

        block.addChild(newBlock);
        newBlock.addPredecessor(block);
        blocks.add(newBlock);

        // Step 4: Edit the prev block
        block.setInstructions(prevBlock);
        int lastInstructionOffset = prevBlock.size() - 1;
        Instruction jump = (Instruction) prevBlock.get(lastInstructionOffset);

        block.setJump(jump);

        // Step 5: edit the offsetToBlock Thing
        for (Object instruction : subBlock) {
            Instruction inst = (Instruction) instruction;
            offsetToBlock.put(inst.offset, newBlock);
        }

        for (Object instruction : prevBlock) {
            Instruction inst = (Instruction) instruction;
            offsetToBlock.put(inst.offset, block);
        }
    }

    public void link() {
        for (BasicBlock block : blocks) {
            Instruction jump = block.getJump();
            if (isControlFlowAltering(jump)) {
                int offset = Integer.parseInt(jump.literal);
                BasicBlock jumpedTo = getBlockFromOffset(offset);

                if (block.getJump().type == OpCode.OP_JUMP_IF_FALSE)
                        jumpedTo.setEdgeType(BasicBlock.EdgeType.False);
                if (block.getJump().type == OpCode.OP_LOOP)
                        jumpedTo.setLoop(true);
                block.addChild(jumpedTo);
                jumpedTo.addPredecessor(block);
            }
        }
    }
}
