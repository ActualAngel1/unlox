import java.util.List;
import java.util.Stack;

public class function {
    final String name;
    List<Instruction> instructions;
    final List<Integer> lines;
    Stack<Value> locals;
    List<String> globals;
    int argCount;

    function(String name, List<Instruction> instructions, List<Integer> lines,
             Stack<Value> stack, List<String> globals, int argCount) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
        this.locals = stack;
        this.globals = globals;
        this.argCount = argCount;
        locals.push(new Value(name, name, true));
    }
}
