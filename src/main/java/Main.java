import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Set<String> commands = Set.of("cd", "dir", "echo", "exit", "set", "cls", "copy", "del", "move", "ren", "pause", "title", "help", "type");

        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();

            // Split input into command and arguments
            String[] tokens = input.split("\\s+", 2);
            String command = tokens[0];
            String argument = tokens.length > 1 ? tokens[1] : "";


            switch(command){
                case "exit":{
                    System.out.println("Exiting the program");
                    scanner.close();
                    return;
                }

                case "echo":{
                    System.out.println(argument);
                    break;
                }

                case "type":{

                        if(commands.contains(argument)){
                            System.out.println(argument + " is a shell builtin");
                            break;
                        }
                        else{
                            String path = getPath(argument);
                            if (path != null) {
                                System.out.println(argument + " is " + path);
                            } 
                            else {
                                System.out.println(argument + ": not found");
                            }
                            break;
                        }
                }
                default:{
                    System.out.println(command + ": command not found");
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
}