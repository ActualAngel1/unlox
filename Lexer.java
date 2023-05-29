import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

class Lexer {
    private final String[] source;
    private final List<function> functions = new ArrayList<>();
    private int current = 0;
    private int offset = 0;
    private List<Integer> currentLines;

    Lexer(String source) {
        this.source = source.split("\n");
        while(!isAtEnd()) {
            functions.add(lexFunction());
        }
    }

    public List<function> getFunctions() {
        return functions;
    }

    public function getFunction() {
        return functions.get(0);
    }

    private boolean isAtEnd() {
        return (current == source.length-1);
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

    private function getCurrentFunction() {
        return functions.get(functions.size()-1);
    }


    private List<Integer> decompress(List<Integer> lines) {
        // This function is needed to decompress the line-encoding compression algorithm I used in
        // the bytecode format.
        List<Integer> decompressed = new ArrayList<>();
        for (int i = 0; i < lines.size(); i+=2) {
            int line = lines.get(i);
            int count = lines.get(i+1);
            for (int j = 0; j < count; j++) {
                decompressed.add(line);
            }
        }

        this.currentLines = decompressed;
        return decompressed;
    }


    private function lexFunction() {

        String name = lexFunctionName();
        current++; // Accounts for the ---------- in the bytecode format
        current++;
        List<Integer> lines = lexFunctionLines();
        current++;
        lexConstants();
        current++;
        List<Instruction> instructions = lexInstructions();

        return new function(name, instructions, lines, new Stack<>(), new ArrayList<>(), 0);
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
        while(!isInstructionPart())
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
        // TODO: TEND TO THIS
        return new Instruction(code, offset-1, lexeme, currentLines.get(0));
    }

    private Instruction scanInstruction() {
        String currentInstruction = source[current];
        offset++;
        switch (currentInstruction) {
            case "OP_CONSTANT":
                this.current+=2;
                offset+=2;
                return createInstruction(OpCode.OP_CONSTANT, source[current]);
            case "OP_NIL":
                return createInstruction(OpCode.OP_NIL, "nil");
            case "OP_TRUE":
                return createInstruction(OpCode.OP_TRUE,  "true");
            case "OP_FALSE":
                return createInstruction(OpCode.OP_FALSE,  "false");
            case "OP_POP":
                return createInstruction(OpCode.OP_POP, "");
            case "OP_GET_LOCAL":
                return createInstruction(OpCode.OP_GET_LOCAL, "");
            case "OP_SET_LOCAL":
                return createInstruction(OpCode.OP_EQUAL, "=");
            case "OP_GET_GLOBAL":
                return createInstruction(OpCode.OP_GET_GLOBAL, source[current]);
            case "OP_DEFINE_GLOBAL":
                return createInstruction(OpCode.OP_DEFINE_GLOBAL,"=");
            case "OP_SET_GLOBAL":
                return createInstruction(OpCode.OP_EQUAL, "=");
            case "OP_EQUAL":
                return createInstruction(OpCode.OP_EQUAL, "==");
            case "OP_GREATER":
                return createInstruction(OpCode.OP_GREATER, ">");
            case "OP_LESS":
                return createInstruction(OpCode.OP_LESS, "<");
            case "OP_ADD":
                return createInstruction(OpCode.OP_ADD, "+");
            case "OP_SUBTRACT":
                return createInstruction(OpCode.OP_SUBTRACT, "-");
            case "OP_MULTIPLY":
                return createInstruction(OpCode.OP_MULTIPLY, "*");
            case "OP_DIVIDE":
                return createInstruction(OpCode.OP_DIVIDE, "/");
            case "OP_NOT":
                return createInstruction(OpCode.OP_NOT, "!");
            case "OP_NEGATE":
                return createInstruction(OpCode.OP_NEGATE, "-");
            case "OP_PRINT":
                return createInstruction(OpCode.OP_PRINT, "print");
            case "OP_JUMP":
                return createInstruction(OpCode.OP_JUMP,  "");
            case "OP_JUMP_IF_FALSE":
                return createInstruction(OpCode.OP_JUMP_IF_FALSE,  "");
            case "OP_LOOP":
                return createInstruction(OpCode.OP_LOOP,  "");
            case "OP_CALL":
                return createInstruction(OpCode.OP_CALL,  "");
            case "OP_RETURN":
                return createInstruction(OpCode.OP_RETURN,  "return");
            case "":
                return createInstruction(OpCode.OP_NO_INSTRUCTION, "");

            default:
                return createInstruction(OpCode.OP_LEXME, source[current]);
        }

    }
}