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

        for (function func : functions) {
            func = new Simplify(func, names).getSimplifiedFunction();
            printFunction(func);
        }

        for (function func : functions) {
            IRtoBB ir = new IRtoBB(func.getInstructions());
            ir.transform();
            ir.split();
            ir.link();
            ir.printBlocks();
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