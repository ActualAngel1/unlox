import java.util.*;

class Lexer {
    private final String[] source;
    private final List<function> functions = new ArrayList<>();
    private int current = 0;
    private int offset = 0;
    private List<Integer> currentLines;
    private static final Map<OpCode, String> map;
    static {
        map = new HashMap<>();
        map.put(OpCode.OP_NIL, "nil");
        map.put(OpCode.OP_TRUE,  "true");
        map.put(OpCode.OP_FALSE,  "false");
        map.put(OpCode.OP_POP, "");
        map.put(OpCode.OP_SET_LOCAL, "=");
        map.put(OpCode.OP_SET_GLOBAL, "=");
        map.put(OpCode.OP_DEFINE_GLOBAL,"=");
        map.put(OpCode.OP_EQUAL, "==");
        map.put(OpCode.OP_GREATER, ">");
        map.put(OpCode.OP_LESS, "<");
        map.put(OpCode.OP_ADD, "+");
        map.put(OpCode.OP_SUBTRACT, "-");
        map.put(OpCode.OP_MULTIPLY, "*");
        map.put(OpCode.OP_DIVIDE, "/");
        map.put(OpCode.OP_NOT, "!");
        map.put(OpCode.OP_NEGATE, "-");
        map.put(OpCode.OP_PRINT, "print");
        map.put(OpCode.OP_CALL,  "");
        map.put(OpCode.OP_RETURN,  "return");
    }

    Lexer(String source) {
        this.source = source.split("\n");
        while(!isAtEnd()) {
            functions.add(lexFunction());
        }
    }

    public List<function> getFunctions() {
        return functions;
    }


    private function lexFunction() {

        String name = lexFunctionName();
        int argCount = Integer.parseInt(source[current]);
        current += 3;
        List<Integer> lines = lexFunctionLines();
        lexConstants();
        List<Instruction> instructions = lexInstructions();

        offset = 0;
        return new function(name, instructions, lines, new Stack<>(), new ArrayList<>(), argCount);
    }

    private String lexFunctionName() {
        // Accounts for the "function" part of the function declaration in
        // the bytecode format, "function" length being 9.
        String name = source[current].substring(9);
        current++;
        return name;
    }

    private List<Integer> lexFunctionLines() {
        List<Integer> lines = new ArrayList<>();
        while (!isConstantPart()) {
            lines.add(Integer.parseInt(source[current]));
            current++;
        }

        return decompress(lines);
    }

    private void lexConstants() {
        do {
            current++;
        } while (!isInstructionPart());
        current++;
    }

    private List<Instruction> lexInstructions() {
        List<Instruction> list = new ArrayList<>();

        while (!isAtEnd() && !isAtFunctionEnd()) {
            list.add(scanInstruction());
            current++;
        }

        return list;
    }

    private Instruction createInstruction(OpCode code, String lexeme) {
        // Fix later
        Instruction instruction = new Instruction(code, offset, lexeme, 1);
        offset++;
        return instruction;
    }

    private Instruction constantInstruction(OpCode code, String lexeme) {
        Instruction instruction = new Instruction(code, offset, lexeme, currentLines.get(0));
        offset+=2;
        return instruction;
    }

    private Instruction jumpInstruction(OpCode code, String lexeme) {
        Instruction instruction = new Instruction(code, code == OpCode.OP_LOOP ? offset - Integer.parseInt(lexeme) : offset, lexeme, currentLines.get(0));
        offset+=3;
        return instruction;
    }

    private Instruction createReturnInstruction(OpCode code, String lexeme) {
        // fix later
        return new Instruction(code, offset, lexeme, 2);
    }

    private Instruction scanInstruction() {
        String currentInstruction = source[current];
        switch (currentInstruction) {

            case "OP_NIL", "OP_TRUE", "OP_FALSE", "OP_POP", "OP_GET_LOCAL", "OP_SET_LOCAL", "OP_GET_GLOBAL", "OP_DEFINE_GLOBAL", "OP_SET_GLOBAL", "OP_EQUAL", "OP_GREATER", "OP_LESS", "OP_ADD", "OP_SUBTRACT", "OP_MULTIPLY", "OP_DIVIDE", "OP_NOT", "OP_NEGATE", "OP_PRINT" -> {
                OpCode code = OpCode.valueOf(currentInstruction);
                return createInstruction(code, map.get(code));
            }

            case "OP_CALL" -> {
                OpCode opCode = OpCode.valueOf(currentInstruction);
                this.current++;
                return constantInstruction(opCode, source[current]);
            }

            case "OP_RETURN" -> {
                OpCode code = OpCode.valueOf(currentInstruction);
                return createReturnInstruction(code, map.get(code));
            }


            case "OP_CONSTANT" -> {
                OpCode opCode = OpCode.valueOf(currentInstruction);
                this.current += 2;
                return constantInstruction(opCode, source[current]);
            }


            case "OP_JUMP", "OP_JUMP_IF_FALSE", "OP_LOOP" -> {
                OpCode opcode = OpCode.valueOf(currentInstruction);
                this.current++;
                return jumpInstruction(opcode, source[current]);
            }


            case "" -> {
                return createInstruction(OpCode.OP_NO_INSTRUCTION, "");
            }


            default -> {
                return createInstruction(OpCode.OP_LEXME, source[current]);
            }
        }

    }

    private List<Integer> decompress(List<Integer> lines) {
        // This function is needed to decompress the line-encoding compression algorithm I used in
        // the bytecode format.

        List<Integer> decompressed = new ArrayList<>();
        for (int i = 0; i < lines.size(); i+=2) {
            int line = lines.get(i);

            // built in max size of 10 thousand instructions
            int count = Math.min(lines.get(i+1), 10000);
            for (int j = 0; j < count; j++) {
                decompressed.add(line);
            }
        }
        // TODO: THIS LINE IS A JOKE
        decompressed.add(decompressed.get(decompressed.size()-1));

        this.currentLines = decompressed;
        return decompressed;
    }

    private boolean isAtEnd() {
        return (current >= source.length);
    }

    private boolean isAtFunctionEnd() {
        return (source[current].contains("function"));
    }

    private boolean isConstantPart() {
        return source[current].equals("constants:");
    }

    private boolean isInstructionPart() {
        return source[current].equals("instructions:");
    }
}