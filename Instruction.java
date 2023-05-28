class Instruction {
    final OpCode type;
    final int index;
    final String literal;
    final int line;

    Instruction(OpCode type, int index, String literal, int line) {
        this.type = type;
        this.index = index;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + literal;
    }
}