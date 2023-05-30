import java.util.List;
import java.util.Random;
import java.util.Stack;

public class function {
    final String name;
    List<Instruction> instructions;
    final List<Integer> lines;
    Stack<String> locals;
    List<String> globals;
    int argCount;

    function(String name, List<Instruction> instructions, List<Integer> lines,
             Stack<String> locals, List<String> globals, int argCount) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
        this.locals = locals;
        this.globals = globals;
        this.argCount = argCount;
        locals.push(name);
        for (int i = 0; i < argCount; i++) {
            locals.push("arg_" + generateString());
        }
    }

    private String generateString() {
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
