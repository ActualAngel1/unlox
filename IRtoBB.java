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

    private boolean isInstructionControlFlowAltering(Instruction instruction) {
        return (instruction.type == OpCode.OP_JUMP ||
                instruction.type == OpCode.OP_JUMP_IF_FALSE ||
                instruction.type == OpCode.OP_LOOP ||
                instruction.type == OpCode.OP_RETURN);
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

    // split()

    // link()


}
