import java.util.*;

public class ExpressionDecompiler {
    private static final Map<OpCode, Integer> map;
    static {
         /*
                PREC_NONE,
                PREC_ASSIGNMENT,  // =
                PREC_OR,          // or
                PREC_AND,         // and
                PREC_EQUALITY,    // == !=
                PREC_COMPARISON,  // < > <= >=
                PREC_TERM,        // + -
                PREC_FACTOR,      // * /
                PREC_UNARY,       // ! -
                PREC_CALL,        // . ()
                PREC_PRIMARY
         */
        map = new HashMap<>();
        map.put(OpCode.OP_SET_LOCAL, 10);
        map.put(OpCode.OP_SET_GLOBAL, 10);
        map.put(OpCode.OP_DEFINE_GLOBAL, 10);
        map.put(OpCode.OP_NOT_EQUAL, 6);
        map.put(OpCode.OP_EQUAL, 6);
        map.put(OpCode.OP_GREATER_EQUAL, 5);
        map.put(OpCode.OP_LESS_EQUAL, 5);
        map.put(OpCode.OP_GREATER, 5);
        map.put(OpCode.OP_LESS, 5);
        map.put(OpCode.OP_ADD, 4);
        map.put(OpCode.OP_SUBTRACT, 4);
        map.put(OpCode.OP_MULTIPLY, 3);
        map.put(OpCode.OP_DIVIDE, 3);
        map.put(OpCode.OP_NOT, 2);
        map.put(OpCode.OP_NEGATE, 2);
    }
    public void decompile(List<BasicBlock> blocks) {
        for (BasicBlock block : blocks) {
            decompileExpressions(block);
        }
    }

    private void decompileExpressions(BasicBlock block) {
        List<Instruction> instructions = transform(block);
        List<Object> exprs = new ArrayList<>(decompileExpressions(instructions));
        List<Object> expressionStmts = new ArrayList<>();
        for (Object expr : exprs) {
            Expr expression = (Expr) expr;
            expressionStmts.add(new Stmt.Expression(expression));
        }

        block.setInstructions(expressionStmts);
    }

    // make it into a list of tokens, exclude the jump instructions from this.
    // Logic: this is an expression decompiler, we can abstract away the instructions making the expressions into just expressions
    // we need to do stuff like: if op_less then op_pop make it into a >= token
    private List<Instruction> transform(BasicBlock block) {
        List<Instruction> instructions = new ArrayList<>();

        for (Object instruction : block.getInstructions()) {
            instructions.add((Instruction) instruction);
        }

        // This function will make cases like op_not, op_less into: op_greater_equal
        // Also this function will abstract away the jumps since they are not needed
        List<Instruction> result = new ArrayList<>();
        int i;
        for (i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);

            if (i == instructions.size()-1) {
                result.add(instruction);
                continue;
            }

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

        return result;
    }

    private List<Expr> decompileExpressions(List<Instruction> instructions) {
        Stack<Expr> stack = new Stack<>();

        for (Instruction instruction : instructions) {
            if (isControlFlowAltering(instruction)) continue;
            if (instruction.type == OpCode.OP_POP) continue;

            if (isConstant(instruction)) {
                literal(stack, instruction);
            } else if (isUnary(stack, instruction)){
                unary(stack, instruction);
            } else if (instruction.type == OpCode.OP_SET_GLOBAL || instruction.type == OpCode.OP_SET_LOCAL) {
                set(stack, instruction);
            } else if (instruction.type == OpCode.OP_DEFINE_GLOBAL) {
                assign(stack);
            } else if (instruction.type == OpCode.OP_PRINT) {
                printExpr(stack);
            } else if (instruction.type == OpCode.OP_RETURN) {
                returnExpr(stack);
            } else if (instruction.type == OpCode.OP_CALL) {
                callExpr(stack, instruction);
            } else {
                binary(stack, instruction);
            }
        }

        return new ArrayList<>(stack);
    }

    private static void set(Stack<Expr> stack, Instruction instruction) {
        Expr name = stack.pop();
        Expr value = stack.pop();

        stack.push(new Expr.Binary(name, instruction, value));
    }

    private static void callExpr(Stack<Expr> stack, Instruction instruction) {
        List<Expr> arguments = new ArrayList<>();
        int argCount = Integer.parseInt(instruction.literal);
        for (int i = 0; i < argCount; i++) {
            arguments.add(stack.pop());
        }

        Collections.reverse(arguments);

        Expr funcName = stack.pop();

        stack.push(new Expr.Call(funcName, instruction, arguments));
    }

    private static void returnExpr(Stack<Expr> stack) {
        stack.push(new Expr.Return(stack.pop()));
    }

    private static void printExpr(Stack<Expr> stack) {
        stack.push(new Expr.Print(stack.pop()));
    }
    private static void assign(Stack<Expr> stack) {
        Expr name = stack.pop();
        Expr value = stack.pop();
        stack.push(new Expr.Assign(name, value));
    }
    private static void binary(Stack<Expr> stack, Instruction instruction) {
        if (stack.size() < 2) {
            System.out.println(instruction);
            System.out.println(stack);
            throw new RuntimeException("This is not a valid RPN Representation");
        }

        // Applying grouping inferring on left and right nodes
        Expr rightNode = stack.pop();
        if (rightNode instanceof Expr.Binary rightExpr) {
            if (map.get(rightExpr.operator.type) > map.get(instruction.type)) {
                rightNode = new Expr.Grouping(rightNode);
            }
        }

        Expr leftNode = stack.pop();
        if (leftNode instanceof Expr.Binary leftExpr) {
            if (map.get(leftExpr.operator.type) > map.get(instruction.type)) {
                leftNode = new Expr.Grouping(leftNode);
            }
        }

        stack.push(new Expr.Binary(leftNode, instruction, rightNode));
    }

    private static void unary(Stack<Expr> stack, Instruction instruction) {
        stack.push(new Expr.Unary(instruction, stack.pop()));
    }

    private static void literal(Stack<Expr> stack, Instruction instruction) {
        stack.push(new Expr.Literal(instruction.literal));
    }

    private boolean isUnary(Stack<Expr> stack, Instruction instruction) {
        return (instruction.type == OpCode.OP_NOT ||
                instruction.type == OpCode.OP_NEGATE ||
                instruction.type == OpCode.OP_SUBTRACT && stack.size() == 1);
    }

    private boolean isConstant(Instruction instruction) {
        return (instruction.type == OpCode.OP_LEXME ||
                instruction.type == OpCode.OP_CONSTANT ||
                instruction.type == OpCode.OP_FALSE ||
                instruction.type == OpCode.OP_TRUE ||
                instruction.type == OpCode.OP_NIL);
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
