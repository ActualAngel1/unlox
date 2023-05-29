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
                    globals.set(Integer.parseInt(instructions.get(i+1).literal), instructions.get(i+2).literal);
                    break;
                case OP_DEFINE_GLOBAL:
                    globals.add(Integer.parseInt(instructions.get(i+1).literal), instructions.get(i+2).literal);
                    break;
                case OP_GET_LOCAL:
                    int index = Integer.parseInt(instructions.get(i+1).literal);
                    if (!(index > locals.size()-1)) {
                        locals.set(index, new Value(generateString(), locals.get(index).value, true));
                        simplified.add(new Instruction(OpCode.OP_LEXME, instructions.get(i + 1).offset, locals.get(index).value, instructions.get(i + 1).line));
                        break;
                    }
                    func.argCount++;
                    locals.push(new Value("arg_" + generateString(), "nil", true ));
                    break;
                case OP_NO_INSTRUCTION:
                    continue;

                default:
                    simplified.add(instruction);
            }
        }

        return simplified;
    }
}
