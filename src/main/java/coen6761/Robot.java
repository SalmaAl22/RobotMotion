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

    private boolean penDown;

    public void init(int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("Size must be > 0");
        }

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

    public void paint() {

        int indexWidth = String.valueOf(floorSize - 1).length();
        for (int r = floorSize - 1; r >= 0; r--) {
            String label = String.valueOf(r);
            System.out.print(label);
            System.out.print(" ".repeat(indexWidth - label.length() + 1));
            for (int c = 0; c < floorSize; c++) {
                char mark = (floor[r][c] == 1) ? '*' : ' ';
                System.out.print(mark);
                System.out.print(" ".repeat(indexWidth));
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.print(" ".repeat(indexWidth + 1));
        for (int c = 0; c < floorSize; c++) {
            String label = String.valueOf(c);
            System.out.print(label);
            System.out.print(" ".repeat(indexWidth - label.length() + 1));
        }
        System.out.println();
    }

    public void moveForward(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Steps must be >= 0");
        }

        int dRow = 0;
        int dCol = 0;
        switch (direction) {
            case NORTH:
                dRow = 1;
                break;
            case EAST:
                dCol = 1;
                break;
            case SOUTH:
                dRow = -1;
                break;
            case WEST:
                dCol = -1;
                break;
        }

        int targetRow = row + (dRow * n);
        int targetCol = col + (dCol * n);
        if (targetRow < 0 || targetRow >= floorSize || targetCol < 0 || targetCol >= floorSize) {
            throw new IllegalArgumentException("Move would go out of bounds");
        }

        for (int step = 0; step < n; step++) {
            row += dRow;
            col += dCol;
            if (penDown) {
                floor[row][col] = 1;
            }
        }
    }
}
