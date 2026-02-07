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

}
