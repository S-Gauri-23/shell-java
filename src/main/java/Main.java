import java.nio.file.Path;
import java.util.Scanner;
import java.io.File;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Set<String> commands = Set.of("echo", "exit", "type", "ls");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            // checks if client wants to exit
            if (input.equals("exit 0")) break;

                // checks if client wants to print some thing
            else if (input.startsWith("echo")) {
                System.out.println(input.substring(5));
            }

            // To handle type command
            else if (input.startsWith("type")) {
                String inputCommand = input.substring(5);
                if (commands.contains(inputCommand)) {
                    String path = findCommandInPath(inputCommand);

                    if (path != null) {
                        System.out.println(inputCommand + " is " + path);
                    } else {
                        System.out.println(inputCommand + ": not found");
                    }
                } else {
                    System.out.println(inputCommand + ": command not found");
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
}