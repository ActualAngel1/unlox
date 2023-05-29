import java.util.*;

public class Simplifiy {
    function func;
    List<Instruction> instructions;
    Stack<Value> locals;
    List<String> globals;
    String name;

    Simplifiy(function func) {
        this.func = func;
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
                    locals.push(new Value("", instruction.literal, false));
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

                case OP_DEFINE_GLOBAL:
                    globals.add(Integer.parseInt(instructions.get(i+1).literal), instructions.get(i+2).literal);
                    simplified.add(instructions.get(i));
                    break;

                case OP_GET_LOCAL:
                    int indexOf = Integer.parseInt(instructions.get(i+1).literal);

                    if (local(indexOf)) {
                        parseLocal(indexOf, i, simplified);
                        break;
                    }

                    parseArgument();
                    break;

                case OP_NO_INSTRUCTION:
                    break;

                default:
                    simplified.add(instruction);
            }
        }

        return simplified;
    }

    private boolean local(int index) {
        return !(index > locals.size()-1);
    }

    private void parseArgument() {
        func.argCount++;
        locals.push(new Value("arg_" + generateString(), "nil", true ));
    }

    private void parseLocal(int indexOf, int i, List<Instruction> simplified) {
        Value newValue = new Value("local_" + generateString(), locals.get(indexOf).value, true);
        locals.set(indexOf, newValue);

        int line = instructions.get(i + 1).line;
        String val = locals.get(indexOf).value;
        int offset = instructions.get(i + 1).offset;
        simplified.add(new Instruction(OpCode.OP_LEXME, offset, val, line));
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
