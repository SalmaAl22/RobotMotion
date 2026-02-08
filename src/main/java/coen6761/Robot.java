package coen6761;

public class Robot {

    // initialize the floor
    private int[][] floor;
    private int floorSize;

    private int row;
    private int col;

    private RobotDirection direction;

    public enum RobotDirection {
        NORTH, EAST, SOUTH, WEST
    }

    boolean penDown;

    public void init(int n) {

        floor = new int[n][n];
        floorSize = n;
        row = 0;
        col = 0;
        penDown = false;

        direction = RobotDirection.NORTH;

    }

    public String getStatus() {
        return "Position: " + row + ", " + col
                + " - Pen: " + (penDown ? "down" : "up")
                + " - Facing: " + direction.toString().toLowerCase();
    }

    public void turnRight() {
        switch (direction) {
            case NORTH:
                direction = RobotDirection.EAST;
                break;
            case EAST:
                direction = RobotDirection.SOUTH;
                break;
            case SOUTH:
                direction = RobotDirection.WEST;
                break;
            case WEST:
                direction = RobotDirection.NORTH;
                break;
        }
    }

    public void turnLeft() {
        switch (direction) {
            case NORTH:
                direction = RobotDirection.WEST;
                break;
            case WEST:
                direction = RobotDirection.SOUTH;
                break;
            case SOUTH:
                direction = RobotDirection.EAST;
                break;
            case EAST:
                direction = RobotDirection.NORTH;
                break;
        }
    }

    public void penDown() {
        penDown = true;
    }

    public void penUp() {
        penDown = false;
    }
}
