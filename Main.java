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

        // Maps a name to a function
        // Map<String, function> names = new HashMap<>();

        for (function func : lexer.getFunctions()) {
            names.add(func.name);

            System.out.println();
            System.out.println("function: " + func.name + " arity " + func.argCount);
            System.out.println("-----------");
            for (Instruction instruction : func.instructions) {
                System.out.println(instruction.toString());
            }
        }
        System.out.println("After simplification: \n");
        for (function func : lexer.getFunctions()) {
            func.instructions = new Simplifiy(func, names).SimplifyBytecode();
            System.out.println();
            System.out.println("function: " + func.name);
            System.out.println("-----------");
            for (Instruction instruction : func.instructions) {
                System.out.println(instruction.toString());
            }

            for (String local : func.locals) {
                System.out.println("Local: " + local);
            }

            for (String global : func.globals) {
                System.out.println("Global: " + global);
            }
        }
    }
}