import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
//         Uncomment this block to pass the first stage
         Scanner scanner = new Scanner(System.in);

         while(true){
            System.out.print("$ ");
            String input = scanner.nextLine();

            //checks if client wants to exit
            if(input.equals("exit 0")) break;

            //checks if client wants to print some thing
            else if (input.startsWith("echo")) {
                System.out.println(input.substring(5));
            } else {
                System.out.println(input + ": command not found");
            }
         }
    }
}
