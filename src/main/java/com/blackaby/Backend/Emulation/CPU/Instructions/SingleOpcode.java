package com.blackaby.Backend.Emulation.CPU.Instructions;

/**
 * A class representing an opcode with a single byte.
 * This class is used to represent an 8-bit instruction opcode for the Z80 CPU.
 * It has a start, middle and end section.
 * The start and end sections determine the instruction, while the middle
 * section
 * is used to determine things like registers and such
 */
public class SingleOpcode extends Opcode {
    private int startCount;
    private int middleCount;
    private int endCount;
    private byte start;
    private byte end;

    /**
     * Get the start and end of the opcode.
     * 
     * @param opcode         The opcode to get the start and end of.
     * @param startCount     The number of bits in the start.
     * @param middleBitCount The number of bits in the middle.
     * @param endCount       The number of bits in the end.
     * @return The start and end of the opcode.
     */
    public static byte[] getStartAndEnd(byte opcode, int startCount, int middleBitCount, int endCount) {
        byte start = (byte) ((opcode >> (8 - startCount)) & ((1 << startCount) - 1));
        byte end = (byte) (opcode & ((1 << endCount) - 1));
        return new byte[] { start, end };
    }

    /**
     * Create a new single opcode with the given opcode, start count, middle bit
     * count and end count.
     * 
     * @param opcode         The opcode.
     * @param startCount     The number of bits in the start.
     * @param middleBitCount The number of bits in the middle.
     * @param endCount       The number of bits in the end.
     */
    public SingleOpcode(byte opcode, int startCount, int middleBitCount, int endCount) {
        super(1, opcode);

        this.startCount = startCount;
        this.middleCount = middleBitCount;
        this.endCount = endCount;
        byte startAndEnd[] = getStartAndEnd(opcode, startCount, middleBitCount, endCount);
        this.start = startAndEnd[0];
        this.end = startAndEnd[1];
    }

    /**
     * Override the matches method to match the opcode.
     * 
     * @param opcode The opcode to match.
     * @return True if the opcode matches, false otherwise.
     */
    @Override
    public boolean matches(byte[] opcode) {
        if (opcode.length != 1) {
            return false;
        }
        return matches(opcode[0]);
    }

    /**
     * Check if the given single-byte opcode matches this opcode.
     * 
     * @param opcode The opcode to check.
     * @return True if the opcode matches, false otherwise.
     */
    public boolean matches(byte opcode) {

        byte startAndEnd[] = getStartAndEnd(opcode, startCount, middleCount, endCount);
        byte start = startAndEnd[0];
        byte end = startAndEnd[1];

        if (start != this.start || end != this.end) {
            return false;
        }

        return true;
    }
}
