package coen6761;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    // ---------- helpers to access private fields ----------
    private int[][] getFloor(Robot robot) {
        try {
            Field f = Robot.class.getDeclaredField("floor");
            f.setAccessible(true);
            return (int[][]) f.get(robot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- init ----------------

    @Test
    void init_validSize_setsDefaults() {
        Robot robot = new Robot();
        robot.init(5);

        assertEquals(
                "Position: 0, 0 - Pen: up - Facing: north",
                robot.getStatus()
        );
    }

    @Test
    void init_invalidSize_throwsException() {
        Robot robot = new Robot();
        assertThrows(IllegalArgumentException.class, () -> robot.init(0));
        assertThrows(IllegalArgumentException.class, () -> robot.init(-1));
    }

    // ---------------- pen up / down ----------------

    @Test
    void penDown_updatesStatus() {
        Robot robot = new Robot();
        robot.init(5);

        robot.penDown();

        assertEquals(
                "Position: 0, 0 - Pen: down - Facing: north",
                robot.getStatus()
        );
    }

    @Test
    void penUp_afterPenDown_updatesStatus() {
        Robot robot = new Robot();
        robot.init(5);

        robot.penDown();
        robot.penUp();

        assertEquals(
                "Position: 0, 0 - Pen: up - Facing: north",
                robot.getStatus()
        );
    }

    // ---------------- turning ----------------

    @Test
    void turnRight_fullCycle() {
        Robot robot = new Robot();
        robot.init(5);

        robot.turnRight();
        assertTrue(robot.getStatus().endsWith("Facing: east"));

        robot.turnRight();
        assertTrue(robot.getStatus().endsWith("Facing: south"));

        robot.turnRight();
        assertTrue(robot.getStatus().endsWith("Facing: west"));

        robot.turnRight();
        assertTrue(robot.getStatus().endsWith("Facing: north"));
    }

    @Test
    void turnLeft_fullCycle() {
        Robot robot = new Robot();
        robot.init(5);

        robot.turnLeft();
        assertTrue(robot.getStatus().endsWith("Facing: west"));

        robot.turnLeft();
        assertTrue(robot.getStatus().endsWith("Facing: south"));

        robot.turnLeft();
        assertTrue(robot.getStatus().endsWith("Facing: east"));

        robot.turnLeft();
        assertTrue(robot.getStatus().endsWith("Facing: north"));
    }

    // ---------------- moveForward ----------------

    @Test
    void moveForward_north_penUp_movesCorrectly() {
        Robot robot = new Robot();
        robot.init(5);

        robot.moveForward(3);

        assertEquals(
                "Position: 3, 0 - Pen: up - Facing: north",
                robot.getStatus()
        );
    }

    @Test
    void moveForward_east_penUp_movesCorrectly() {
        Robot robot = new Robot();
        robot.init(5);

        robot.turnRight(); // east
        robot.moveForward(2);

        assertEquals(
                "Position: 0, 2 - Pen: up - Facing: east",
                robot.getStatus()
        );
    }

    @Test
    void moveForward_negativeSteps_throws() {
        Robot robot = new Robot();
        robot.init(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> robot.moveForward(-1)
        );
    }

    @Test
    void moveForward_outOfBounds_throws() {
        Robot robot = new Robot();
        robot.init(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> robot.moveForward(5)
        );
    }

    // Covers all direction cases in moveForward() switch
    @Test
    void moveForward_allDirections_hitAllSwitchCases() {
        Robot robot = new Robot();
        robot.init(5);

        // NORTH: (0,0) -> (1,0)
        robot.moveForward(1);
        assertEquals("Position: 1, 0 - Pen: up - Facing: north", robot.getStatus());

        // EAST: (1,0) -> (1,1)
        robot.turnRight(); // east
        robot.moveForward(1);
        assertEquals("Position: 1, 1 - Pen: up - Facing: east", robot.getStatus());

        // SOUTH: (1,1) -> (0,1)
        robot.turnRight(); // south
        robot.moveForward(1);
        assertEquals("Position: 0, 1 - Pen: up - Facing: south", robot.getStatus());

        // WEST: (0,1) -> (0,0)
        robot.turnRight(); // west
        robot.moveForward(1);
        assertEquals("Position: 0, 0 - Pen: up - Facing: west", robot.getStatus());
    }

    @Test
    void moveForward_outOfBounds_south_throws() {
        Robot robot = new Robot();
        robot.init(5);

        robot.turnRight();
        robot.turnRight(); // south

        assertThrows(IllegalArgumentException.class, () -> robot.moveForward(1));
    }

    // ---------------- drawing ----------------

    @Test
    void moveForward_penDown_drawsPath() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();

        robot.moveForward(2);

        int[][] floor = getFloor(robot);

        assertEquals(1, floor[0][0]);
        assertEquals(1, floor[1][0]);
        assertEquals(1, floor[2][0]);
    }

    // ---------------- paint ----------------

    @Test
    void paint_withoutInit_throwsException() {
        Robot robot = new Robot();
        assertThrows(IllegalStateException.class, robot::paint);
    }

    @Test
    void paint_printsGrid() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));

        try {
            robot.paint();
        } finally {
            System.setOut(old);
        }

        String output = out.toString();
        assertTrue(output.contains("*"));
        assertTrue(output.contains("0"));
    }
}
