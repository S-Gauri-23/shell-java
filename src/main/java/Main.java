import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Set<String> commands = Set.of("cd", "dir", "echo", "exit", "set", "cls", "copy", "del", "move", "ren", "pause", "title", "help", "type", "pwd");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Split input into command and arguments
            String[] tokens = input.split("\\s+");
            String command = tokens[0];
            String[] arguments = new String[tokens.length-1];
            System.arraycopy(tokens, 1, arguments, 0, arguments.length);

            switch(command){
                case "exit":{
                    scanner.close();
                    return;
                }

                case "echo":{
                    System.out.println(String.join(" ", arguments));
                    break;
                }

                case "type":{
                    if(commands.contains(arguments[0])){
                        System.out.println(arguments[0] + " is a shell builtin");
                        break;
                    }
                    else{
                        String path = getPath(arguments[0]);
                        if (path != null) {
                            System.out.println(arguments[0] + " is " + path);
                        }
                        else {
                            System.out.println(arguments[0] + ": not found");
                        }
                        break;
                    }
                }

                case "pwd":{
                    getWorkingDirectory();
                    break;
                }

                default:{
                    executeCommand(command, arguments);
                    break;
                }
            }
        }
    }

    private static String getPath(String parameter) {
        String pathSeparator = File.pathSeparator;
        String[] paths = System.getenv("PATH").split(pathSeparator);
        String[] extensions = {"", ".exe", ".bat", ".cmd"};

        for (String path : paths) {
            for (String ext : extensions) {
                Path fullPath = Path.of(path, parameter + ext);      
                if (Files.isRegularFile(fullPath) && Files.isExecutable(fullPath)) {
                    return fullPath.toString();
                }
            }
        }
        return null;
    }

    private static void executeCommand(String command, String[] args) {
        if (command == null || command.isBlank()) {
            System.out.println("Error: Command is empty or null.");
            return;
        }
    
        String path = getPath(command);
        if (path == null) {
            System.out.println(command + ": command not found");
            return;
        }
    
        try {
            // Construct full command with arguments
            String[] fullCommand = new String[args.length + 1];
            fullCommand[0] = command; // Use full path instead of just the command
            System.arraycopy(args, 0, fullCommand, 1, args.length);
    
            // Execute the command
            ProcessBuilder pb = new ProcessBuilder(fullCommand);
            pb.inheritIO(); // Ensure output is printed to console
            Process process = pb.start();
            int exitCode = process.waitFor();
    
            if (exitCode != 0) {
                System.out.println(command + ": command execution failed with exit code " + exitCode);
            }
        } catch (IOException e) {
            System.out.println(command + ": command execution failed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String getWorkingDirectory() {
        String path = Paths.get("").toAbsolutePath().toString();
        return path;
    }
}