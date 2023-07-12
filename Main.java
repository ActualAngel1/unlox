import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Please input a valid path to the bytecode file you wish to decompile: ");
        Scanner scanner = new Scanner(System.in);
        String filePath = scanner.nextLine();

        runFile(filePath);
    }
    private static void runFile(String path) throws IOException {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            run(new String(bytes, Charset.defaultCharset()));
        } catch (NoSuchFileException e) {
            System.err.println("This path is invalid");
        } catch (AccessDeniedException e) {
            System.err.println("Cannot access this file, " +
                    "try inserting the filename at the end of the path");
        } catch (InvalidPathException e) {
            System.err.println("Illegal character in path");
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer(source);

        // Maps a function name to source
        Set<String> names = new HashSet<>();
        List<function> functions = lexer.getFunctions();

        for (function func : functions) {
            names.add(func.getName());
            printSimpleFunction(func);
        }

        System.out.println("After simplification: \n");

        functions.replaceAll(function -> (new Simplify(function, names)).getSimplifiedFunction());

        for (function func : functions) {
            printFunction(func);
        }

        for (function func : functions) {
            IRtoBB flowGraph = new IRtoBB(func.getInstructions());
            flowGraph.transform();
            flowGraph.split();
            flowGraph.link();
            flowGraph.printBlocks();

            List<BasicBlock> blocks = flowGraph.getBlocks();
            new ExpressionDecompiler().decompile(blocks);
            List<BasicBlock> afterBlocks = new ControlFlowAnalysisPhase(blocks).getBlocks();
            new AstToSource().transformAll(afterBlocks);

            printBlocks(afterBlocks);
        }
    }

    private static void printBlocks(List<BasicBlock> blocks) {
        System.out.println("\n");
        for (int i = 0; i < blocks.size(); i++) {
            System.out.println("id: " + i + " Instructions: " + blocks.get(i));
            System.out.println("Successors: ");
            System.out.println();

            for (BasicBlock block : blocks.get(i).getSuccessors()) {
                System.out.println(blocks.indexOf(block) + ", ");
            }

            System.out.println("Predecessors: ");
            System.out.println();
            for (BasicBlock block : blocks.get(i).getPredecessors()) {
                System.out.println(blocks.indexOf(block) + ", ");
            }

            System.out.println("Pred count:" + blocks.get(i).getPredecessors().size() + " ,... " + blocks.get(i).getSuccessors().size());

            System.out.println();
        }
    }

    private static void printSimpleFunction(function func) {
        System.out.println();
        System.out.println("function: " + func.getName() + " arity " + func.getArgCount());
        System.out.println("-----------");
        for (Instruction instruction : func.getInstructions()) {
            System.out.println(instruction.toString());
        }
    }

    private static void printFunction(function func) {
            System.out.println();
            System.out.println("function: " + func.getName());
            System.out.println("-----------");

            for (Instruction instruction : func.getInstructions()) {
                System.out.println(instruction.toString());
            }

            for (String local : func.getLocals()) {
                System.out.println("Local: " + local);
            }

            for (String global : func.getGlobals()) {
                System.out.println("Global: " + global);
            }
        }
}