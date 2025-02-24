import java.nio.file.Path;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Set<String> commands = Set.of("echo", "exit", "type", "ls", "cat");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Split input into command and arguments
            String[] tokens = input.split("\\s+", 2);
            String command = tokens[0];
            String argument = tokens.length > 1 ? tokens[1] : "";

            // checks if client wants to exit
            if (command.equals("exit")) break;

                // checks if client wants to print some thing
            else if (command.startsWith("echo")) {
                System.out.println(argument);
            }

            // To handle type command
            else if (command.startsWith("type")) {
                // String inputCommand = input.substring(5);
                if (commands.contains(argument)) {
                    String path = findCommandInPath(argument);

                    if (path != null) {
                        System.out.println(argument + " is " + path);
                    } else {
                        System.out.println(argument + ": not found");
                    }
                } else {
                    System.out.println(argument + ": command not found");
                }
            }

            // Handle cat command
            else if (command.equals("cat")) {
                if (argument.isEmpty()) {
                    System.out.println("cat: missing file operand");
                } else {
                    catCommand(argument);
                }
            }
        }
    }

    private static String findCommandInPath(String command) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return null;
        }

        String[] directories = pathEnv.split(";");
        for (String dir : directories) {
            File file = new File(dir, command);
            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    private static void catCommand(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("cat: " + fileName + ": No such file or directory");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("cat: " + fileName + ": Error reading file");
        }
    }
}