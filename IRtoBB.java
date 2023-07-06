import java.util.*;

public class IRtoBB {
    private final List<Instruction> IR;
    private List<BasicBlock> blocks = new ArrayList<>();

    // Maps all instructions to their basic block
    private Map<Integer, BasicBlock> offsetToBlock = new HashMap<>();

    IRtoBB(List<Instruction> IR) {
        this.IR = IR;
    }

    public void transform() {
        List<Instruction> block = new ArrayList<>();
        Stack<Instruction> stack = new Stack<>();

        for (Instruction current : IR) {
            stack.push(current);

            if (isInstructionControlFlowAltering(current)) {
                block.addAll(stack);
                BasicBlock currBlock = new BasicBlock(block, current);

                if (!blocks.isEmpty()) {
                    BasicBlock prevBlock = blocks.get(blocks.size() - 1);
                    linkPrevBlock(currBlock, prevBlock);

                    // TODO: "split" blocks that are jumped to, link all blocks
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

        if (!isJumpInstruction(previous.getJump())) {
            // link the prev block to current block
            previous.addChild(current);
        }
    }

    private void map(List<Instruction> block, BasicBlock blockPointer) {
        for (Instruction instruction : block) {
            offsetToBlock.put(instruction.offset, blockPointer);
        }
    }

    public void printBlocks() {
        for (BasicBlock block : blocks) {
            System.out.println(block);
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

    private void split(int offset) {
        // Step 1: get block
        BasicBlock block = offsetToBlock.get(offset);
        List<Instruction> subBlock = new ArrayList<>();
        List<Instruction> prevBlock = new ArrayList<>();

        // Step 2: create the new block
        for (Instruction instruction : block.getInstructions()) {
            if (instruction.offset >= offset) {
                subBlock.add(instruction);
            } else {
                prevBlock.add(instruction);
            }
        }

        if (prevBlock.isEmpty()) return;

        // Step 3: Link and add the new block to the block list
        BasicBlock newBlock = new BasicBlock(subBlock, block.getJump());
        block.addChild(newBlock);
        blocks.add(newBlock);

        // Step 4: Edit the prev block
        block.setInstructions(prevBlock);
        int lastInstructionOffset = prevBlock.size() - 1;
        Instruction jump = prevBlock.get(lastInstructionOffset);

        block.setJump(jump);

        // Step 5: edit the offsetToBlock Thing
        for (Instruction instruction : subBlock) {
            offsetToBlock.put(instruction.offset, newBlock);
        }

        for (Instruction instruction : prevBlock) {
            offsetToBlock.put(instruction.offset, block);
        }
    }

    public void link() {
        for (BasicBlock block : blocks) {
            Instruction jump = block.getJump();
            if (isControlFlowAltering(jump)) {
                int offset = Integer.parseInt(jump.literal);
                BasicBlock jumpedTo = offsetToBlock.get(offset);

                block.addChild(jumpedTo);
            }
        }
    }


}
