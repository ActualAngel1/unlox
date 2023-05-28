import java.util.HashMap;
import java.util.Map;
/*
public class Lexer {
    /*
    private static final Map<String, OpCode> map;
    OP_LESS,
    OP_ADD, OP_SUBTRACT, OP_MULTIPLY, OP_DIVIDE,
    OP_NOT, OP_NEGATE, OP_PRINT, OP_JUMP, OP_JUMP_IF_FALSE, OP_LOOP, OP_CALL, OP_RETURN,
    static {
        map = new HashMap<>();
        map.put("OP_NIL", OpCode.OP_NIL);
        map.put("OP_TRUE", OpCode.OP_TRUE);
        map.put("OP_FALSE", OpCode.OP_FALSE);
        map.put("OP_POP", OpCode.OP_POP);
        map.put("OP_GET_LOCAL", OpCode.OP_GET_LOCAL);
        map.put("OP_SET_LOCAL", OpCode.OP_SET_LOCAL);
        map.put("OP_GET_GLOBAL", OpCode.OP_GET_GLOBAL);
        map.put("OP_DEFINE_GLOBAL", OpCode.OP_DEFINE_GLOBAL);
        map.put("OP_SET_GLOBAL", OpCode.OP_SET_GLOBAL);
        map.put("OP_EQUAL", OpCode.OP_EQUAL);
        map.put("OP_GREATER", OpCode.OP_GREATER);
        map.put()
    }


    String[] lines = myString.split(System.getProperty("line.separator"));
}

 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Lexer {
    private final String[] source;
    private final List<function> functions = new ArrayList<>();
    private int current = 0;
    private int index = 0;

    Lexer(String source) {
        this.source = source.split("\n");
        while(!isAtEnd()) {
            functions.add(lexFunction());
        }
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

        return new function(name, instructions, lines);
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

    private Instruction scanInstruction() {
        String currentInstruction = source[current];
        switch (currentInstruction) {
            case "OP_NIL":
            case "OP_TRUE":
            case "OP_FALSE":
            case "OP_POP":
            case "OP_GET_LOCAL":
            case "OP_SET_LOCAL":
            case "OP_GET_GLOBAL":
            case "OP_DEFINE_GLOBAL":
            case "OP_SET_GLOBAL":
            case "OP_EQUAL":
            case "OP_GREATER":
            case "OP_LESS":
            case "OP_ADD":
            case "OP_SUBTRACT":
            case "OP_MULTIPLY":
            case "OP_DIVIDE":
            case "OP_NOT":
            case "OP_NEGATE":
            case "OP_PRINT":
            case "OP_JUMP":
            case "OP_JUMP_IF_FALSE":
            case "OP_LOOP":
            case "OP_CALL":
            case "OP_RETURN":
                System.out.println("Instruction: " + currentInstruction);
                index++;
                return new Instruction(OpCode.OP_NIL, index, "NIL", 0);

            default:
                System.out.println("Unknown instruction or space");
                return new Instruction(OpCode.OP_NIL, index, "NIL", 0);
        }
    }
    /*

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case '?': addToken(QUESTION_MARK); break;
            case ':': addToken(COLON); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                }

                else if (match('*')){
                    longComment();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            //literals
            case '"': string(); break;


            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
        }
    }
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }
    private void string(){
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        // The closing "
        advance();

        String value = source.substring(start + 1, current-1);
        addToken(STRING, value);
    }
    private void longComment(){
        while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
            if (peek() == '/' && peekNext() == '*') {advance(); longComment();}
        }
        if(!isAtEnd()) {advance(); advance();}
    }
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
    private char advance() {
        return source.charAt(current++); // is the same as .charAt(current), current++
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

*/
}