import java.util.*;

public class Simplifiy {
    function func;
    List<Instruction> instructions;
    Stack<String> locals;
    List<String> globals;
    String name;

    // Set of function names
    Set<String> names;

    Simplifiy(function func, Set<String> names) {
        this.func = func;
        this.names = names;

        this.instructions = func.instructions;
        this.locals = func.locals;
        this.globals = func.globals;
        this.name = func.name;
    }

    public List<Instruction> SimplifyBytecode() {
        List<Instruction> simplified = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            switch (instruction.type) {
                case OP_CONSTANT:
                    if (isFunction(instruction.literal)) {
                        locals.push(instruction.literal);
                    } else {
                        locals.push("local_" + generateString());
                    }

                    simplified.add(instruction);
                    break;

                case OP_GET_GLOBAL:
                    i+=2;
                    simplified.add(instructions.get(i));
                    break;

                case OP_SET_GLOBAL:
                    int index = Integer.parseInt(instructions.get(i+1).literal);
                    String value = instructions.get(i+2).literal;
                    globals.set(index, value);
                    simplified.add(instructions.get(i));
                    break;

                case OP_POP:

                    if (locals.size() > func.argCount + 1) {
                        locals.pop();
                    }
                    simplified.add(instructions.get(i));
                    break;

                case OP_DEFINE_GLOBAL:
                    globals.add(Integer.parseInt(instructions.get(i+1).literal), instructions.get(i+2).literal);
                    simplified.add(instructions.get(i));
                    break;

                case OP_GET_LOCAL:
                    int indexOf = Integer.parseInt(instructions.get(i+1).literal);

                    parseLocal(indexOf, instruction, simplified);
                    i++;
                    break;

                case OP_NO_INSTRUCTION:
                    break;

                default:
                    simplified.add(instruction);
            }
        }

        return simplified;
    }


    private void parseArgument(int indexOf, Instruction instruction, List<Instruction> simplified) {
        int line = instruction.line;
        int offset = instruction.offset;
        simplified.add(new Instruction(OpCode.OP_LEXME, offset, locals.get(indexOf), line));
    }

    private boolean isFunction(String str) {
        return str.charAt(0) ==  '<' || names.contains(str);
    }

    private void parseLocal(int indexOf, Instruction instruction, List<Instruction> simplified) {
        if (Objects.equals(locals.get(indexOf), "")) {
            String name = "local_" + generateString();
        } else {
            name = locals.get(indexOf);
        }
        locals.set(indexOf, name);

        int line = instruction.line;
        int offset = instruction.offset;
        simplified.add(new Instruction(OpCode.OP_LEXME, offset, name, line));
    }

    public String generateString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 3;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
