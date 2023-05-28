import java.util.List;

public class function {
    final String name;
    final List<Instruction> instructions;
    final List<Integer> lines;

    function(String name, List<Instruction> instructions, List<Integer> lines) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
    }
}
