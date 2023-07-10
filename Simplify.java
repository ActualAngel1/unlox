import java.util.*;

public class Simplify {
    private final function func;
    private List<Instruction> instructions;
    private final Stack<String> locals;
    private final List<String> globals;
    private final String name;

    // Set of function names
    private final Set<String> names;

    Simplify(function func, Set<String> names) {
        this.func = func;
        this.names = names;
        this.locals = func.getLocals();
        this.globals = func.getGlobals();
        this.name = func.getName();
        this.instructions = func.getInstructions();
    }

    public function getSimplifiedFunction() {
        this.instructions = simplifyBytecode();
        return new function(this.name, this.instructions, this.func.getLines(),
                this.locals, this.globals, this.func.getArgCount());
    }

    public List<Instruction> simplifyBytecode() {
        List<Instruction> simplified = new ArrayList<>();
        int instructionCount = instructions.size();

        for (int i = 0; i < instructionCount; i++) {
            Instruction instruction = instructions.get(i);

            switch (instruction.type) {
                case OP_CONSTANT:
                    simplifyOpConstant(instruction, simplified);
                    break;

                case OP_GET_GLOBAL:
                    // i need to set up this stuff
                    i += 2;
                    locals.push(instructions.get(i).literal);
                    simplified.add(instructions.get(i));
                    break;

                case OP_SET_GLOBAL:
                    i+=2;
                    simplified.add(instructions.get(i));
                    i-=2;
                    simplifyOpSetGlobal(i, instruction, simplified);
                    i+=2;
                    break;

                case OP_POP:
                    locals.pop();
                    simplified.add(instruction);
                    break;

                case OP_DEFINE_GLOBAL:
                    simplifyOpDefineGlobal(i, instruction, simplified);
                    i+=2;
                    break;

                case OP_GET_LOCAL:
                    i = simplifyOpGetLocal(i, instruction, simplified);
                    break;

                case OP_SET_LOCAL:
                    simplified.add(instruction);
                    int index = Integer.parseInt(instructions.get(i + 1).literal);
                    String localVar = locals.get(index);
                    Instruction var = new Instruction(OpCode.OP_LEXME, instruction.offset, localVar, instruction.line);
                    simplified.add(i, var);
                    break;

                case OP_NO_INSTRUCTION:
                    break;

                default:
                    simplified.add(instruction);
            }
        }

        return simplified;
    }

    private void simplifyOpConstant(Instruction instruction, List<Instruction> simplified) {
        if (isFunction(instruction.literal)) {
            locals.push(instruction.literal);
        } else {
            locals.push("local_" + generateString());
        }

        simplified.add(instruction);
    }

    private void simplifyOpSetGlobal(int currentIndex, Instruction instruction, List<Instruction> simplified) {
        int index = Integer.parseInt(instructions.get(currentIndex + 1).literal);
        String value = instructions.get(currentIndex + 2).literal;
        if (globals.size() <= index){
            globals.add(value);
        } else {
            globals.set(index, value);
        }
        simplified.add(instruction);
    }

    private void simplifyOpPop(Instruction instruction, List<Instruction> simplified) {
        if (locals.size() - 1 > func.getArgCount() + 1) {
            locals.pop();
        }
        simplified.add(instruction);
    }

    private void simplifyOpDefineGlobal(int currentIndex, Instruction instruction, List<Instruction> simplified) {
        String secondOperand = instructions.get(currentIndex + 2).literal;
        simplified.add(new Instruction(OpCode.OP_LEXME, instruction.offset, secondOperand, instruction.line));
        globals.add(Integer.parseInt(instructions.get(currentIndex + 1).literal), instructions.get(currentIndex + 2).literal);
        simplified.add(instruction);
    }

    private int simplifyOpGetLocal(int currentIndex, Instruction instruction, List<Instruction> simplified) {
        int indexOf = Integer.parseInt(instructions.get(currentIndex + 1).literal);
        parseLocal(indexOf, instruction, simplified);
        return currentIndex + 1;
    }



    private boolean isFunction(String str) {
        return str.charAt(0) ==  '<' || names.contains(str);
    }

    private void parseLocal(int indexOf, Instruction instruction, List<Instruction> simplified) {
        String localName;
        if (Objects.equals(locals.get(indexOf), "")) {
            localName = "local_" + generateString();
        } else {
            localName = locals.get(indexOf);
        }
        locals.set(indexOf, localName);

        int line = instruction.line;
        int offset = instruction.offset;
        simplified.add(new Instruction(OpCode.OP_LEXME, offset, localName, line));
    }

    public String generateString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 3;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
