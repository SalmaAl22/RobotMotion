package coen6761;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dev Team Regression Tests — Task 4 response to QA findings.
 *
 * QA Finding: PIT mutation testing revealed 9 surviving mutants, all in Robot.paint().
 * The prior paint tests only verified that '*' and '0' appear somewhere in the output,
 * which was too coarse to detect structural mutations in loop boundaries, arithmetic,
 * negated conditions, or removed output calls.
 *
 * Each test below is named regression_<area>_<what_it_catches> and is pinned to exactly
 * one or more surviving mutants as documented in the header comment.
 *
 * Surviving mutants targeted (all in Robot.paint()):
 *   M1  line  92  CONDITIONALS_BOUNDARY  r >= 0        → r > 0        (skips row 0)
 *   M2  line 103  CONDITIONALS_BOUNDARY  c < floorSize → c <= floorSize (extra col in footer)
 *   M3  line  89  MATH                   floorSize - 1 → floorSize + 1 (wrong indexWidth)
 *   M4  line 101  MATH                   indexWidth + 1 → indexWidth - 1 (wrong footer padding)
 *   M5  line  95  NEGATE_CONDITIONALS    == 1          → != 1         (inverts marks)
 *   M6  line 103  NEGATE_CONDITIONALS    c < floorSize → c >= floorSize (no footer indices)
 *   M7  line  98  VOID_METHOD_CALLS      removes println() after each row
 *   M8  line 101  VOID_METHOD_CALLS      removes leading-spaces print for footer
 *   M9  line 106  VOID_METHOD_CALLS      removes final println()
 */
class RobotRegressionTest {

    // -----------------------------------------------------------------------
    // Shared helper — captures System.out during paint()
    // -----------------------------------------------------------------------
    private String capturePaint(Robot robot) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));
        try {
            robot.paint();
        } finally {
            System.setOut(old);
        }
        return buf.toString();
    }

    /** Split output into lines, tolerating both LF and CRLF. */
    private String[] splitLines(String output) {
        return output.split("\\r?\\n", -1);
    }

    /** Return the last non-empty line (the footer). */
    private String footerLine(String[] lines) {
        for (int i = lines.length - 1; i >= 0; i--) {
            if (!lines[i].isEmpty()) return lines[i];
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // M1 — CONDITIONALS_BOUNDARY line 92: r >= 0  →  r > 0
    //   Effect: the row loop stops before printing row 0.
    //   Kill strategy: assert row 0 label exists AND its mark is correct.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_row0IsAlwaysPrinted() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(2); // marks floor[0][0], [1][0], [2][0]

        String[] lines = splitLines(capturePaint(robot));

        String row0Line = null;
        for (String line : lines) {
            if (line.startsWith("0 ")) { row0Line = line; break; }
        }

        assertNotNull(row0Line,
                "Row 0 must appear in paint output — loop bound must be r >= 0, not r > 0");
        assertTrue(row0Line.contains("*"),
                "Row 0 must show '*' at col 0 because pen was down at origin");
    }

    // -----------------------------------------------------------------------
    // M2 — CONDITIONALS_BOUNDARY line 103: c < floorSize  →  c <= floorSize
    //   Effect: footer loop prints one extra column index (floorSize).
    //   Kill strategy: assert the last token in the footer equals floorSize-1.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_footerLastColumnIsFloorSizeMinusOne() {
        Robot robot = new Robot();
        robot.init(5); // last valid column = 4

        String footer = footerLine(splitLines(capturePaint(robot)));
        assertNotNull(footer, "Footer line must be present");

        // Trim trailing spaces; the last visible character(s) should be "4"
        String trimmed = footer.stripTrailing();
        assertTrue(trimmed.endsWith("4"),
                "Footer's last column index must be 4 (floorSize-1), not 5. Got: '" + footer + "'");
    }

    // -----------------------------------------------------------------------
    // M3 — MATH line 89: floorSize - 1  →  floorSize + 1
    //   Effect: wrong indexWidth — on n=10, len("11")=2 instead of len("9")=1,
    //           so row labels get an extra leading space.
    //   Kill strategy: on a 10×10 grid, row 0 must start with "0 " not " 0 ".
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_indexWidthIsBasedOnMaxIndexNotMaxIndexPlusTwo() {
        Robot robot = new Robot();
        robot.init(10); // floorSize-1=9 (width 1) vs floorSize+1=11 (width 2)

        String[] lines = splitLines(capturePaint(robot));

        // Find the line for row 0
        String row0Line = null;
        for (String line : lines) {
            if (line.startsWith("0 ")) { row0Line = line; break; }
        }

        assertNotNull(row0Line,
                "Row 0 must start with '0 ' (1-digit label). " +
                "If it starts with ' 0 ', indexWidth was computed from floorSize+1 instead of floorSize-1");
    }

    // -----------------------------------------------------------------------
    // M4 — MATH line 101: indexWidth + 1  →  indexWidth - 1
    //   Effect: footer indent = indexWidth-1 spaces instead of indexWidth+1.
    //           For indexWidth=1: 0 spaces instead of 2 spaces.
    //   Kill strategy: footer must start with exactly (indexWidth+1) spaces.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_footerIndentEqualsIndexWidthPlusOne() {
        Robot robot = new Robot();
        robot.init(5); // indexWidth = len("4") = 1 → footer indent must be 2 spaces

        String footer = footerLine(splitLines(capturePaint(robot)));
        assertNotNull(footer, "Footer line must be present");

        assertTrue(footer.startsWith("  "),
                "Footer must start with 2 spaces (indexWidth+1 = 1+1 = 2). Got: '" + footer + "'");
        // Also confirm it's NOT indented by 0 (mutation result: indexWidth-1 = 0)
        assertFalse(footer.startsWith("0"),
                "Footer must not start with '0' — leading spaces are missing when mutant active");
    }

    // -----------------------------------------------------------------------
    // M5 — NEGATE_CONDITIONALS line 95: floor[r][c] == 1  →  floor[r][c] != 1
    //   Effect: * and space are swapped — unmarked cells show *, marked cells show blank.
    //   Kill strategy: count '*' in the grid body; must equal the number of marked cells.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_onlyMarkedCellsShowAsterisk() {
        Robot robot = new Robot();
        robot.init(5);
        robot.penDown();
        robot.moveForward(2); // exactly 3 cells marked: [0][0], [1][0], [2][0]

        String[] lines = splitLines(capturePaint(robot));

        // Count '*' across the 5 data-row lines (skip the footer = last non-empty)
        long asterisks = 0;
        for (int i = 0; i < lines.length; i++) {
            // Skip footer line
            if (lines[i].trim().matches("[0-9 ]+")) continue; // footer has only digits and spaces
            for (char ch : lines[i].toCharArray()) {
                if (ch == '*') asterisks++;
            }
        }

        assertEquals(3, asterisks,
                "Exactly 3 cells were marked — exactly 3 '*' must appear in the grid body. " +
                "Got " + asterisks + " (if 22, the mark condition was negated)");
    }

    // -----------------------------------------------------------------------
    // M6 — NEGATE_CONDITIONALS line 103: c < floorSize  →  c >= floorSize
    //   Effect: footer loop condition is false from the start — no column indices printed.
    //   Kill strategy: all column indices 0..floorSize-1 must appear in the footer.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_footerContainsAllColumnIndices() {
        Robot robot = new Robot();
        robot.init(5);

        String footer = footerLine(splitLines(capturePaint(robot)));
        assertNotNull(footer, "Footer line must be present");

        for (int c = 0; c < 5; c++) {
            assertTrue(footer.contains(String.valueOf(c)),
                    "Footer must contain column index " + c + ". Got: '" + footer + "'");
        }
    }

    // -----------------------------------------------------------------------
    // M7 — VOID_METHOD_CALLS line 98: removes System.out.println() after each row
    //   Effect: all floorSize rows concatenate onto one line — no per-row line breaks.
    //   Kill strategy: output must contain at least floorSize+1 newlines.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_eachRowTerminatedByNewline() {
        Robot robot = new Robot();
        robot.init(5); // 5 data rows + 1 footer = at least 6 newlines expected

        String output = capturePaint(robot);
        long newlineCount = output.chars().filter(c -> c == '\n').count();

        assertTrue(newlineCount >= 6,
                "paint() must emit at least 6 newlines (one per data row + footer). " +
                "Got " + newlineCount + " — if 1, the per-row println was removed");
    }

    // -----------------------------------------------------------------------
    // M8 — VOID_METHOD_CALLS line 101: removes System.out.print(" ".repeat(indexWidth+1))
    //   Effect: footer has no leading spaces — starts immediately with column index "0".
    //   Kill strategy: footer must begin with at least one space character.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_footerHasLeadingSpacesBeforeColumnIndices() {
        Robot robot = new Robot();
        robot.init(5);

        String footer = footerLine(splitLines(capturePaint(robot)));
        assertNotNull(footer, "Footer line must be present");

        assertTrue(footer.charAt(0) == ' ',
                "Footer must begin with a space (the index-width + 1 padding). " +
                "Got: '" + footer + "' — leading spaces were removed by mutant");
    }

    // -----------------------------------------------------------------------
    // M9 — VOID_METHOD_CALLS line 106: removes the final System.out.println()
    //   Effect: the last line of output has no newline terminator.
    //   Kill strategy: full output string must end with a newline character.
    // -----------------------------------------------------------------------
    @Test
    void regression_paint_outputEndsWithNewline() {
        Robot robot = new Robot();
        robot.init(5);

        String output = capturePaint(robot);

        assertTrue(output.endsWith("\n") || output.endsWith("\r\n"),
                "paint() output must end with a newline. " +
                "The final println() was removed by the surviving mutant");
    }
}
