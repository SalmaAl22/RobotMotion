package coen6761;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RobotTest {

    @Test
    void init_sets_default_status() {
        Robot robot = new Robot();
        robot.init(5);

        assertEquals("Position: 0, 0 - Pen: up - Facing: north", robot.getStatus());
    }
}
