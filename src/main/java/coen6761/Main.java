package coen6761;

import java.util.Scanner;

//import main.java.coen6761.Robot;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the Robot Motion");

        Robot robot = new Robot();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();
            char command = Character.toUpperCase(input.charAt(0));

            switch (command) {

                case 'I': {
                    try {
                        int n = Integer.parseInt(input.substring(1).trim());
                        robot.init(n);
                        System.out.println("System initialized to " + n + " x " + n);
                    } catch (Exception e) {
                        System.out.println("Invalid format. Use: I n (example: I 10)");
                    }
                    break;
                }

                case 'C':
                    System.out.println(robot.getStatus());
                    break;

                // Quit
                case 'Q':
                    System.out.println("End of program.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Unknown command.");

            }

        }

    }
}