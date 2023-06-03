import java.util.List;
import java.util.Random;
import java.util.Stack;

public class function {
    private final String name;
    private final List<Integer> lines;
    private List<Instruction> instructions;
    private Stack<String> locals;
    private List<String> globals;
    private final int argCount;

    function(String name, List<Instruction> instructions, List<Integer> lines,
             Stack<String> locals, List<String> globals, int argCount) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
        this.locals = locals;
        this.globals = globals;
        this.argCount = argCount;
        addLocal(name);

        for (int i = 0; i < argCount; i++) {
            addLocal(generateArgVarName());
        }
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
    }

    public void addGlobal(String string) {
        globals.add(string);
    }

    public void addLocal(String string) {
        locals.push(string);
    }

    public String getName() {
        return this.name;
    }

    public int getArgCount() {
        return argCount;
    }

    public Stack<String> getLocals() {
        return locals;
    }


    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void setGlobals(List<String> globals) {
        this.globals = globals;
    }

    public void setLocals(Stack<String> locals) {
        this.locals = locals;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<Integer> getLines() {
        return lines;
    }

    public List<String> getGlobals() {
        return globals;
    }

    private String generateArgVarName() {
        return("arg_" + generateString());
    }

    private String generateLocalVarName() {
        return("local_" + generateString());
    }

    private String generateString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 3;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
