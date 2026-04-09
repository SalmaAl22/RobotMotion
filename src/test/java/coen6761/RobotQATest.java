package coen6761;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QA Self-Review Test Suite
 *
 * Covers:
 *   a) Statement coverage
 *   b) Decision coverage
 *   c) Condition coverage
 *   d) Multiple condition coverage for: if (input == null || input.trim().isEmpty())
 *   e) Data flow tests for moveForward(int n)
 */
class RobotQATest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private int[][] getFloor(Robot robot) {
        try {
            Field f = Robot.class.getDeclaredField("floor");
            f.setAccessible(true);
            return (int[][]) f.get(robot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes Main.executeCommand(String, Robot) via reflection.
     * Targets the compound guard: if (input == null || input.trim().isEmpty())
     */
    private void callExecuteCommand(String input, Robot robot) {
        try {
            Method m = Main.class.getDeclaredMethod("executeCommand", String.class, Robot.class);
            m.setAccessible(true);
            m.invoke(null, input, robot);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            throw new RuntimeException(cause);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------
    // a) STATEMENT COVERAGE
    // -----------------------------------------------------------------------

    /**
     * SC-1: execute_validCommand_shouldUpdateState
     * Verifies that a valid command string correctly reaches and updates robot state.
     * Covers the executeCommand dispatch path (case 'R').
     */
    @Test
    void execute_validCommand_shouldUpdateState() {
        Robot robot = new Robot();
        robot.init(10);
        // "R" → turnRight → facing east
        callExecuteCommand("R", robot);
        assertTrue(robot.getStatus().endsWith("Facing: east"),
                "After 'R' command robot should face east");
    }

    /**
     * SC-2: move_zeroSteps_shouldNotChangePosition
     * Verifies that moveForward(0) executes without error and leaves position unchanged.
     * Covers the loop body (step=0 → loop never entered) and pre-loop boundary check.
     */
    @Test
    void move_zeroSteps_shouldNotChangePosition() {
        Robot robot = new Robot();
        robot.init(5);
        robot.moveForward(0);
        assertEquals("Position: 0, 0 - Pen: up - Facing: north", robot.getStatus(),
                "Zero steps should not change position");
    }

    // -----------------------------------------------------------------------
    // b) DECISION COVERAGE
    // -----------------------------------------------------------------------

    /**
     * DC-1: move_withPenDown_shouldMarkFloor
     * Decision: if (penDown) → TRUE branch.
     * Verifies that floor cells are marked when the pen is down during movement.
     */
    @Test
    void move_withPenDown_shouldMarkFloor() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(2);

        int[][] floor = getFloor(robot);
        assertEquals(1, floor[0][0], "Start cell should be marked");
        assertEquals(1, floor[1][0], "Cell after step 1 should be marked");
        assertEquals(1, floor[2][0], "Cell after step 2 should be marked");
    }

    /**
     * DC-2: move_withPenUp_shouldNotMarkFloor
     * Decision: if (penDown) → FALSE branch.
     * Verifies that floor cells are NOT marked when the pen is up during movement.
     */
    @Test
    void move_withPenUp_shouldNotMarkFloor() {
        Robot robot = new Robot();
        robot.init(5);
        // pen is up by default
        robot.moveForward(2);

        int[][] floor = getFloor(robot);
        assertEquals(0, floor[0][0], "Start cell should NOT be marked");
        assertEquals(0, floor[1][0], "Cell after step 1 should NOT be marked");
        assertEquals(0, floor[2][0], "Cell after step 2 should NOT be marked");
    }

    /**
     * DC-3: init_validSize_shouldResetState
     * Decision: if (n <= 0) → FALSE branch (valid size allowed).
     * Verifies that a valid re-init resets position, direction, pen, and floor completely.
     */
    @Test
    void init_validSize_shouldResetState() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.turnRight();
        robot.moveForward(2);

        // Re-init with larger size — everything should reset
        robot.init(10);
        assertEquals("Position: 0, 0 - Pen: up - Facing: north", robot.getStatus(),
                "Re-init should reset position, pen, and direction");
        int[][] floor = getFloor(robot);
        assertEquals(10, floor.length,   "Floor should be 10x10");
        assertEquals(10, floor[0].length, "Floor should be 10x10");
        assertEquals(0, floor[0][0], "Floor cell should be cleared after re-init");
    }

    /**
     * DC-4: init_invalidSize_shouldThrow
     * Decision: if (n <= 0) → TRUE branch (exception thrown).
     * Verifies that sizes ≤ 0 are rejected with IllegalArgumentException.
     */
    @Test
    void init_invalidSize_shouldThrow() {
        Robot robot = new Robot();
        assertThrows(IllegalArgumentException.class, () -> robot.init(0),
                "Size 0 must throw");
        assertThrows(IllegalArgumentException.class, () -> robot.init(-5),
                "Negative size must throw");
    }

    // -----------------------------------------------------------------------
    // c) CONDITION COVERAGE  +  d) MULTIPLE CONDITION COVERAGE
    //    Target condition: if (input == null || input.trim().isEmpty())
    //
    //    MC-1: null=true,  empty=N/A  → execute_nullInput_shouldDoNothing
    //    MC-2: null=false, empty=true → execute_blankInput_shouldDoNothing
    //    MC-3: null=false, empty=false→ execute_validInput_shouldWork
    // -----------------------------------------------------------------------

    /**
     * CC/MCC-1: execute_nullInput_shouldDoNothing
     * Condition: (null == null) → true  →  short-circuit, method returns immediately.
     * Robot state must remain unchanged.
     */
    @Test
    void execute_nullInput_shouldDoNothing() {
        Robot robot = new Robot();
        robot.init(5);
        String statusBefore = robot.getStatus();

        callExecuteCommand(null, robot);   // null → left operand true → returns

        assertEquals(statusBefore, robot.getStatus(),
                "Null input must not change robot state");
    }

    /**
     * CC/MCC-2: execute_blankInput_shouldDoNothing
     * Condition: (input==null) → false, (input.trim().isEmpty()) → true  →  returns.
     * Robot state must remain unchanged.
     */
    @Test
    void execute_blankInput_shouldDoNothing() {
        Robot robot = new Robot();
        robot.init(5);
        String statusBefore = robot.getStatus();

        callExecuteCommand("   ", robot);  // blank → right operand true → returns

        assertEquals(statusBefore, robot.getStatus(),
                "Blank input must not change robot state");
    }

    /**
     * CC/MCC-3: execute_validInput_shouldWork
     * Condition: (input==null) → false, (input.trim().isEmpty()) → false  →  processes.
     * Robot state must change to reflect the command.
     */
    @Test
    void execute_validInput_shouldWork() {
        Robot robot = new Robot();
        robot.init(5);

        callExecuteCommand("L", robot);    // both conditions false → turn left

        assertTrue(robot.getStatus().endsWith("Facing: west"),
                "Valid 'L' command should turn robot left to west");
    }

    // -----------------------------------------------------------------------
    // e) DATA FLOW TESTS for moveForward(int n)
    //
    // DU pairs exercised:
    //   steps (n)       : defined at entry, used in loop condition step < n
    //   step (i)        : defined at 0, used in condition and incremented each iter
    //   targetCol (nextX): defined as col+(dCol*n), used in boundary check
    //   targetRow (nextY): defined as row+(dRow*n), used in boundary check
    //   col (x)         : updated each step (col+=dCol), used in floor[row][col]=1
    //   row (y)         : updated each step (row+=dRow), used in floor[row][col]=1
    //   penDown         : set via penDown(), used as guard in floor marking
    //   floor[row][col] : assigned=1 when pen down, asserted in tests
    // -----------------------------------------------------------------------

    /**
     * DF-1: DU pair — steps (n)
     * n is defined at method entry; used in "step < n" loop condition.
     * With n=3, the loop must iterate exactly 3 times → row=3.
     */
    @Test
    void dataflow_steps_definedAtEntry_usedInLoopCondition() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(3);
        assertTrue(robot.getStatus().contains("Position: 3, 0"),
                "n=3 steps should move robot to row 3");
    }

    /**
     * DF-2: DU pair — step (i)
     * step is defined as 0, used in "step < n" condition, and incremented each iteration.
     * With n=4 and pen down, all 5 cells (rows 0-4) must be marked.
     */
    @Test
    void dataflow_i_definedAtZero_usedInLoopConditionAndIncrement() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(4);
        int[][] floor = getFloor(robot);
        for (int r = 0; r <= 4; r++) {
            assertEquals(1, floor[r][0],
                    "floor[" + r + "][0] should be marked (loop incremented " + r + " times)");
        }
    }

    /**
     * DF-3: DU pair — targetCol (nextX), within-bounds path
     * targetCol is defined as col+(dCol*n), used in boundary check (not thrown),
     * and then col converges to targetCol via step-by-step increments.
     */
    @Test
    void dataflow_nextX_withinBounds_assignedToCol() {
        Robot robot = new Robot();
        robot.init(5);
        robot.turnRight();          // face east → dCol=1
        robot.moveForward(3);       // targetCol = 0 + 1*3 = 3 (in bounds)
        assertTrue(robot.getStatus().contains("Position: 0, 3"),
                "targetCol=3 is in bounds; robot should reach col=3");
    }

    /**
     * DF-4: DU pair — targetCol (nextX), out-of-bounds path
     * targetCol is defined, used in boundary check → throws; col must not be updated.
     */
    @Test
    void dataflow_nextX_outOfBounds_throwsBeforeColUpdate() {
        Robot robot = new Robot();
        robot.init(5);
        robot.turnRight();                          // face east → dCol=1
        String statusBefore = robot.getStatus();
        assertThrows(IllegalArgumentException.class, () -> robot.moveForward(5),
                "targetCol=5 is out of bounds; should throw");
        assertEquals(statusBefore, robot.getStatus(),
                "col must not change when boundary check fails");
    }

    /**
     * DF-5: DU pair — targetRow (nextY), within-bounds path
     * targetRow is defined as row+(dRow*n), passes boundary check, robot reaches it.
     */
    @Test
    void dataflow_nextY_withinBounds_assignedToRow() {
        Robot robot = new Robot();
        robot.init(5);
        robot.moveForward(4);           // face north, targetRow = 0 + 1*4 = 4
        assertTrue(robot.getStatus().contains("Position: 4, 0"),
                "targetRow=4 is in bounds; robot should reach row=4");
    }

    /**
     * DF-6: DU pair — targetRow (nextY), out-of-bounds path
     * targetRow = 0 + (-1)*1 = -1 → boundary check throws; row must not update.
     */
    @Test
    void dataflow_nextY_outOfBounds_throwsBeforeRowUpdate() {
        Robot robot = new Robot();
        robot.init(5);
        robot.turnRight(); robot.turnRight();       // face south → dRow=-1
        String statusBefore = robot.getStatus();
        assertThrows(IllegalArgumentException.class, () -> robot.moveForward(1),
                "targetRow=-1 is out of bounds; should throw");
        assertEquals(statusBefore, robot.getStatus(),
                "row must not change when boundary check fails");
    }

    /**
     * DF-7: DU pair — col (x)
     * col is updated each step (col+=dCol), then used as index in floor[row][col]=1.
     * Moving east with pen down: floor[0][0..3] should all be marked.
     */
    @Test
    void dataflow_col_updatedEachStep_usedInFloorMarking() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.turnRight();              // face east
        robot.moveForward(3);

        int[][] floor = getFloor(robot);
        assertEquals(1, floor[0][0], "floor[0][0] must be marked");
        assertEquals(1, floor[0][1], "floor[0][1] must be marked");
        assertEquals(1, floor[0][2], "floor[0][2] must be marked");
        assertEquals(1, floor[0][3], "floor[0][3] must be marked");
    }

    /**
     * DF-8: DU pair — row (y)
     * row is updated each step (row+=dRow), then used as index in floor[row][col]=1.
     * Moving north with pen down: floor[0..3][0] should all be marked.
     */
    @Test
    void dataflow_row_updatedEachStep_usedInFloorMarking() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(3);           // face north

        int[][] floor = getFloor(robot);
        assertEquals(1, floor[0][0], "floor[0][0] must be marked");
        assertEquals(1, floor[1][0], "floor[1][0] must be marked");
        assertEquals(1, floor[2][0], "floor[2][0] must be marked");
        assertEquals(1, floor[3][0], "floor[3][0] must be marked");
    }

    /**
     * DF-9: DU pair — penDown
     * penDown is set via penDown()/penUp(), then read as guard "if (penDown)".
     * Tests both the true path (marks floor) and false path (leaves floor clean).
     */
    @Test
    void dataflow_penDown_setViaPenDown_usedInFloorMarkingCondition() {
        Robot robot = new Robot();
        robot.init(5);

        // FALSE path: pen up → no marks
        robot.moveForward(2);
        int[][] floor = getFloor(robot);
        assertEquals(0, floor[0][0], "Pen-up move: floor should not be marked");
        assertEquals(0, floor[1][0], "Pen-up move: floor should not be marked");

        // TRUE path: pen down → marks
        robot.init(5);
        robot.penDown();
        robot.moveForward(2);
        floor = getFloor(robot);
        assertEquals(1, floor[0][0], "Pen-down move: floor should be marked");
        assertEquals(1, floor[1][0], "Pen-down move: floor should be marked");
        assertEquals(1, floor[2][0], "Pen-down move: floor should be marked");
    }

    /**
     * DF-10: DU pair — floor[row][col]
     * floor[row][col] is assigned=1 when penDown is true (def), then read in assertions (use).
     * Mixed-pen path: marks only the cells where pen was down.
     */
    @Test
    void dataflow_floorCell_assignedWhenPenDown_verifiedByAssertion() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(1);   // marks floor[0][0] and floor[1][0]
        robot.penUp();
        robot.moveForward(1);   // floor[2][0] NOT marked

        int[][] floor = getFloor(robot);
        assertEquals(1, floor[0][0], "floor[0][0] marked (pen was down at start)");
        assertEquals(1, floor[1][0], "floor[1][0] marked (pen was down during step 1)");
        assertEquals(0, floor[2][0], "floor[2][0] NOT marked (pen was up during step 2)");
    }
}
