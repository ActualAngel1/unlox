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
    private boolean isAtEnd = false;
    private final List<function> functions = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Lexer(String source) {
        this.source = source.split("\n");
        while(!isAtEnd) {
            functions.add(lexFunction());
        }
    }
}