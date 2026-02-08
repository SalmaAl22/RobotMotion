package coen6761;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//import main.java.coen6761.Robot;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the Robot Motion");

        Robot robot = new Robot();
        Scanner scanner = new Scanner(System.in);
        boolean initialized = false;
        List<String> history = new ArrayList<>();

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Please enter a command.");
                continue;
            }

            char command = Character.toUpperCase(input.charAt(0));

            // Quit should always be allowed
            if (command == 'Q') {
                System.out.println("End of program.");
                scanner.close();
                return;
            }

            if (command == 'I') {
                try {
                    int n = Integer.parseInt(input.substring(1).trim());
                    robot.init(n);
                    initialized = true;
                    System.out.println("System initialized to " + n + " x " + n);
                } catch (Exception e) {
                    System.out.println("Invalid format. Use: I n (example: I 10)");
                }
                continue;
            }

            if (initialized == false) {
                System.out.println("Please Initialize first!");
                continue;
            }

            if (command != 'H' && command != 'Q') {

                history.add(input);

            }

            switch (command) {
                case 'R':
                    robot.turnRight();
                    break;

                case 'L':
                    robot.turnLeft();
                    break;

                case 'U':
                    robot.penUp();
                    break;

                case 'D':
                    robot.penDown();
                    break;

                case 'C':
                    System.out.println(robot.getStatus());
                    break;

                case 'P':
                    robot.paint();
                    break;

                case 'H':
                    if (history.isEmpty()) {
                        System.out.println("No history to replay.");
                        break;
                    }

                    System.out.println(
                            "(Automated replay of the steps in history since the start of the program)");

                    for (String past : history) {
                        executeCommand(past, robot);
                    }

                    break;

                case 'M':
                    try {
                        int n = Integer.parseInt(input.substring(1).trim());
                        robot.moveForward(n);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid format. Use: M n (example: M 10)");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Move error: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Unknown command.");
            }

        }

    }

    private static void executeCommand(String input, Robot robot) {

        input = input.trim();
        if (input.isEmpty())
            return;

        char command = Character.toUpperCase(input.charAt(0));

        switch (command) {

            case 'R':
                robot.turnRight();
                break;
            case 'L':
                robot.turnLeft();
                break;
            case 'U':
                robot.penUp();
                break;
            case 'D':
                robot.penDown();
                break;
            case 'C':
                System.out.println(robot.getStatus());
                break;
            case 'P':
                robot.paint();
                break;

            case 'M':
                try {
                    int n = Integer.parseInt(input.substring(1).trim());
                    robot.moveForward(n);
                } catch (Exception e) {
                    System.out.println("Move error.");
                }
                break;

            default:
                System.out.println("Unknown command.");
        }
    }
}
