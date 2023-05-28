import java.util.List;

public class function {
    final String name;
    final List<Instruction> instructions;
    final int[] lines;

    function(String name, List<Instruction> instructions, int[] lines) {
        this.name = name;
        this.instructions = instructions;
        this.lines = lines;
    }
}
