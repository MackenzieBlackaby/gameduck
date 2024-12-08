package com.blackaby.Backend.Emulation.CPU.Instructions;

/**
 * A class representing an opcode.
 * This class has multiple sub classes, however this basic class is used to
 * represent
 * an opcode where the bits represent all one part.
 */
public class Opcode {
    /**
     * An enum representing the length of an opcode.
     * This is used to determine how many bytes an opcode is.
     */
    public enum OpcodeLength {
        SingleByte(1),
        DoubleByte(2),;

        private int length;

        /**
         * Create a new opcode length with the given length.
         * 
         * @param length The length of the opcode.
         */
        OpcodeLength(int length) {
            this.length = length;
        }

        /**
         * Get the length of the opcode.
         * 
         * @return The length of the opcode.
         */
        public int getLength() {
            return length;
        }

        /**
         * Get the opcode length from the given length.
         * 
         * @param length The length of the opcode.
         * @return The opcode length.
         */
        public static OpcodeLength fromLength(int length) {
            for (OpcodeLength opcodeLength : OpcodeLength.values()) {
                if (opcodeLength.getLength() == length) {
                    return opcodeLength;
                }
            }
            return null;
        }
    }

    private byte[] opcode;
    protected OpcodeLength length;

    /**
     * Create a new opcode with the given length and opcode.
     * 
     * @param length The length of the opcode.
     * @param opcode The opcode.
     */
    public Opcode(int length, byte... opcode) {
        this.opcode = opcode;
        this.length = OpcodeLength.fromLength(length);
    }

    /**
     * Get the opcode.
     * 
     * @return The opcode.
     */
    public byte[] getOpcode() {
        return opcode;
    }

    /**
     * Get the length of the opcode.
     * 
     * @return The length of the opcode.
     */
    public OpcodeLength getLength() {
        return length;
    }

    /**
     * Check if the given opcode matches this opcode.
     * 
     * @param opcode The opcode to check.
     * @return True if the opcode matches, false otherwise.
     */
    public boolean matches(byte[] opcode) {
        if (opcode.length != this.opcode.length) {
            return false;
        }

        for (int i = 0; i < opcode.length; i++) {
            if (opcode[i] != this.opcode[i]) {
                return false;
            }
        }

        return true;
    }
}
