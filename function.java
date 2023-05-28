import java.util.List;
import java.util.Stack;

public class function {
    final String name;
    final List<Instruction> instructions;
    final List<Integer> lines;
    Stack<String> locals;
    List<String> globals;

    function(String name, List<Instruction> instructions, List<Integer> lines, Stack<String> stack, List<String> globals) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
        this.locals = stack;
        this.globals = globals;
    }
}
