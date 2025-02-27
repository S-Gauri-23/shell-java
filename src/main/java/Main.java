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
                
                    StringBuilder result = new StringBuilder();
                    boolean inSingleQuotes = false, inDoubleQuotes = false;
                    boolean escapeNext = false;
                
                    for (int i = 0; i < inputText.length(); i++) {
                        char c = inputText.charAt(i);
                
                        if (escapeNext) {
                            result.append(c); // Always keep escaped characters
                            escapeNext = false;
                        } else if (c == '\\') {
                            escapeNext = true; // Enable escaping next character
                        } else if (c == '"' && !inSingleQuotes) {
                            inDoubleQuotes = !inDoubleQuotes; // Toggle double-quoted state
                            result.append(c); // Keep quotes in the output
                        } else if (c == '\'' && !inDoubleQuotes) {
                            inSingleQuotes = !inSingleQuotes; // Toggle single-quoted state
                        } else {
                            result.append(c);
                        }
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
                    if (input.length() < 5) {  // If no arguments are given
                        System.out.println("cat: missing file operand");
                        break;
                    }
                
                    List<String> fileNames = new ArrayList<>();
                    StringBuilder currentFileName = new StringBuilder();
                    boolean inSingleQuotes = false, inDoubleQuotes = false;
                    boolean escapeNext = false;
                
                    char[] chars = input.substring(4).trim().toCharArray(); // Extract only `cat ...`
                
                    for (int i = 0; i < chars.length; i++) {
                        char c = chars[i];
                
                        if (escapeNext) {
                            // Handle escaped sequences properly
                            switch (c) {
                                case 'n': currentFileName.append("\\n"); break; // Preserve \n as part of the filename
                                case 't': currentFileName.append("\\t"); break; // Preserve \t as part of the filename
                                case '\\': currentFileName.append("\\"); break;  // Preserve literal backslash
                                case '"': currentFileName.append("\""); break;   // Preserve double quote
                                case '\'':
                                    if (inDoubleQuotes) {
                                        currentFileName.append("'"); // Preserve single quote inside double quotes
                                    } else {
                                        inSingleQuotes = !inSingleQuotes; // Toggle single-quoted state
                                    }
                                    break;
                                default:
                                    if (Character.isDigit(c) && i + 2 < chars.length && Character.isDigit(chars[i + 1]) && Character.isDigit(chars[i + 2])) {
                                        // Preserve octal sequences (\33, \37)
                                        String octal = "" + c + chars[i + 1] + chars[i + 2];
                                        currentFileName.append("\\").append(octal); // Keep it in filename format
                                        i += 2; // Skip next two characters
                                    } else {
                                        currentFileName.append("\\").append(c); // Keep invalid escapes
                                    }
                            }
                            escapeNext = false;
                        } else if (c == '\\') {
                            escapeNext = true; // Enable escaping next character
                        } else if (c == '"' && !inSingleQuotes) {
                            inDoubleQuotes = !inDoubleQuotes; // Toggle double-quoted state
                        } else if (c == '\'' && !inDoubleQuotes) {
                            inSingleQuotes = !inSingleQuotes; // Toggle single-quoted state
                        } else if (c == ' ' && !inSingleQuotes && !inDoubleQuotes) {
                            if (currentFileName.length() > 0) {
                                fileNames.add(currentFileName.toString());
                                currentFileName.setLength(0);
                            }
                        } else {
                            currentFileName.append(c);
                        }
                    }
                
                    if (currentFileName.length() > 0) {
                        fileNames.add(currentFileName.toString());
                    }
                
                    if (inSingleQuotes || inDoubleQuotes) {
                        System.out.println("cat: unmatched quote error");
                        break;
                    }
                
                    for (String fileName : fileNames) {
                        Path filePath = Path.of(fileName);
                
                        if (!Files.exists(filePath)) {
                            System.out.println("cat: " + fileName + ": No such file or directory");
                            continue;
                        }
                
                        try {
                            List<String> lines = Files.readAllLines(filePath);
                            for (String line : lines) {
                                System.out.print(line + " "); // Maintain expected output format
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