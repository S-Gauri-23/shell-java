import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                    String currentDir = System.getProperty("user.dir");
                    System.out.println(currentDir);
                    break;
                }

                case "cd":{
                    String newDirectory = arguments[0];

                    // Storing the new directory path in a File object
                    File directory = new File(newDirectory);

                    // Check if the directory exists and is a valid directory
                    // then change the directory
                    if(directory.exists() && directory.isDirectory()){
                        System.setProperty("user.dir", newDirectory);
                    }
                    else{
                        System.out.println(command + ": " + directory + ": No such file or directory");
                    }
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
            if (path.isBlank()) continue;

            for (String ext : extensions) {
                Path fullPath = Path.of(path.trim(), parameter + ext);      
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
}