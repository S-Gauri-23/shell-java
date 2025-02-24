import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {

         Scanner scanner = new Scanner(System.in);
        Set<String> commands = Set.of("echo", "exit", "type");

         while(true){
            System.out.print("$ ");
            String input = scanner.nextLine();

            // checks if client wants to exit
            if(input.equals("exit 0")) break;

            // checks if client wants to print some thing
            else if (input.startsWith("echo")) {
                System.out.println(input.substring(5));
            }

            //
            else if(input.startsWith("type")){
                String inputCommand = input.substring(5);
                if(commands.contains(inputCommand)){
                    System.out.println(inputCommand + " is a shell builtin");
                }
                else{
                    System.out.println(inputCommand + ": not found");
                }
            }
            else {
                System.out.println(input + ": command not found");
            }
         }
    }
}
