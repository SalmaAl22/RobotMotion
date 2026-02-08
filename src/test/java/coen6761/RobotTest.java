package coen6761;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RobotTest {

    @Test
    void penDown_updatesStatus() {
        Robot robot = new Robot();
        robot.init(5);

        robot.penDown();

        String status = robot.getStatus();
        assertTrue(status.contains("Position: 0, 0"));
        assertTrue(status.contains("Pen: down"));
        assertTrue(status.contains("Facing: north"));
    }

    @Test
    void penUp_afterPenDown_updatesStatusBack() {
        Robot robot = new Robot();
        robot.init(5);

        robot.penDown();
        robot.penUp();

        String status = robot.getStatus();
        assertTrue(status.contains("Position: 0, 0"));
        assertTrue(status.contains("Pen: up"));
        assertTrue(status.contains("Facing: north"));
    }
}