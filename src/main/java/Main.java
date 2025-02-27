import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

                case "echo": {
                    String inputText = input.substring(5).trim(); // Extract text after "echo"
                    
                    StringBuilder result = new StringBuilder(); // Stores final output
                    boolean inQuotes = false; // Track if inside single quotes
                    StringBuilder currentPart = new StringBuilder(); // Stores current word or phrase
                
                    for (char c : inputText.toCharArray()) {
                        if (c == '\'') {  
                            inQuotes = !inQuotes; // Toggle between inside and outside quotes
                        } else {
                            if (inQuotes || c != ' ') {
                                currentPart.append(c); // Append character to current part
                            } else {
                                if (currentPart.length() > 0) {
                                    result.append(currentPart).append(" "); // Add to final output
                                    currentPart.setLength(0); // Reset current part
                                }
                            }
                        }
                    }
                
                    if (currentPart.length() > 0) {
                        result.append(currentPart);
                    }
                
                    System.out.println(result.toString().trim()); // Print final result
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

                case "cat": {
                    if (arguments.length == 0) {
                        System.out.println("cat: missing file operand");
                        break;
                    }
                
                    List<String> fileNames = new ArrayList<>();
                    StringBuilder currentFileName = new StringBuilder();
                    boolean inQuotes = false;
                
                    // Correctly reconstruct filenames that are inside quotes
                    for (String arg : arguments) {
                        if (arg.startsWith("'") && !inQuotes) {
                            inQuotes = true;
                            currentFileName.append(arg.substring(1)).append(" "); // Remove starting quote
                        } else if (arg.endsWith("'") && inQuotes) {
                            inQuotes = false;
                            currentFileName.append(arg, 0, arg.length() - 1); // Remove ending quote
                            fileNames.add(currentFileName.toString().trim()); // Add full filename
                            currentFileName.setLength(0); // Reset for next filename
                        } else if (inQuotes) {
                            currentFileName.append(arg).append(" "); // Preserve spaces
                        } else {
                            fileNames.add(arg); // Normal filename without spaces
                        }
                    }
                
                    if (inQuotes) {
                        System.out.println("cat: unmatched single quote error");
                        break;
                    }
                
                    for (String fileName : fileNames) {
                        Path filePath = Path.of(fileName); // Do not append to `user.dir`, absolute paths are already correct
                
                        if (!Files.exists(filePath)) {
                            System.out.println("cat: " + fileName + ": No such file or directory");
                            continue;
                        }
                
                        try {
                            List<String> lines = Files.readAllLines(filePath);
                            for (String line : lines) {
                                System.out.print(line + " "); // Ensure expected output format
                            }
                        } catch (IOException e) {
                            System.out.println("cat: " + fileName + ": Error reading file");
                        }
                    }
                    System.out.println(); // Ensure new line at the end
                    break;
                }                
                

                case "cd":{
                    // getting the actual HOME directory
                    String homeDir = System.getenv("HOME");
                    if(homeDir == null){
                        homeDir = System.getProperty("user.home");
                    }

                    // Navigate to user's home directory
                    if(arguments.length == 0 || arguments[0].equals("~")){
                        System.setProperty("user.dir", homeDir);
                        break;
                    }
                    else{

                        // Get current working directory
                        Path currentDir = Path.of(System.getProperty("user.dir"));

                        // Resolve relative or absolute path correctly
                        Path newPath = currentDir.resolve(arguments[0]).normalize();

                        // Check if the directory exists and is a valid directory
                        // then change the directory
                        if(!Files.exists(newPath) || !Files.isDirectory(newPath)){
                            System.out.println(command + ": " + arguments[0] + ": No such file or directory");
                            break;
                        }

                        // update working directory
                        System.setProperty("user.dir", newPath.toString());
                        break;
                    }
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
            if (path == null || path.isBlank()) continue;

            for (String ext : extensions) {
                Path fullPath = Path.of(path.trim(), parameter + ext);      
                if (Files.exists(fullPath) && Files.isRegularFile(fullPath) && Files.isExecutable(fullPath)) {
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