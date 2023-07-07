import java.util.ArrayList;
import java.util.List;

public class ExpressionDecompiler {
    public void decompile(BasicBlock block) {
        // Decompile this block;
        decompileExpressions(block);
        for (BasicBlock child : block.getSuccessors()) {
            decompile(child);
        }
    }

    private void decompileExpressions(BasicBlock block) {
        List<Instruction> instructions = transform(block);
        List<Object> exprs = new ArrayList<>(decompileExpressions(instructions));

        block.setInstructions(exprs);
    }

    // make it into a list of tokens, exclude the jump instructions from this.
    // Logic: this is an expression decompiler, we can abstract away the instructions making the expressions into just expressions
    // we need to do stuff like: if op_less then op_pop make it into a >= token
    private List<Expr> decompileExpressions(List<Instruction> instructions) {
        List<Expr> expressions = new ArrayList<>();



        return expressions;
    }
    private List<Instruction> transform(BasicBlock block) {
        List<Instruction> instructions = new ArrayList<>();

        for (Object instruction : block.getInstructions()) {
            instructions.add((Instruction) instruction);
        }

        // This function will make cases like op_not, op_less into: op_greater_equal
        // Also this function will abstract away the jumps since they are not needed
        List<Instruction> result = new ArrayList<>();
        int i;
        for (i = 0; i < instructions.size()-2; i++) {
            Instruction instruction = instructions.get(i);
            Instruction nextInstruction = instructions.get(i+1);
            if (isControlFlowAltering(instruction)) break;

            boolean isNextInstructionNot = nextInstruction.type == OpCode.OP_NOT;

            if (isNextInstructionNot && isInstrucionRelevant(instruction)) {
                switch (instruction.type) {
                    case OP_LESS    -> result.add(createInstruction(instruction, OpCode.OP_GREATER_EQUAL, ">="));
                    case OP_GREATER -> result.add(createInstruction(instruction, OpCode.OP_LESS_EQUAL, "<="));
                    case OP_EQUAL   -> result.add(createInstruction(instruction, OpCode.OP_NOT_EQUAL, "!="));
                }
                i++;
            } else {
                result.add(instruction);
            }
        }

        result.add(instructions.get(i+1));

        return result;
    }

    private Instruction createInstruction(Instruction instruction, OpCode code, String literal) {
        return new Instruction(code, instruction.offset, literal, instruction.line);
    }

    private boolean isInstrucionRelevant(Instruction instruction) {
        return (instruction.type == OpCode.OP_LESS ||
                instruction.type == OpCode.OP_GREATER ||
                instruction.type == OpCode.OP_EQUAL);
    }

    private boolean isControlFlowAltering(Instruction instruction) {
        return (instruction.type == OpCode.OP_JUMP ||
                instruction.type == OpCode.OP_JUMP_IF_FALSE ||
                instruction.type == OpCode.OP_LOOP);
    }
}
