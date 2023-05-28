import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        function func = lexer.getFunction();
        for (Integer line : func.lines) {
            System.out.println(line);
        }

    }
}