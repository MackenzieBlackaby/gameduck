package com.blackaby.Backend.Emulation.CPU.Instructions;

/**
 * This enum represents the different types of instructions that the CPU can
 * execute.
 * Each instruction has an opcode and a description.
 * The opcode is the byte that represents the instruction in memory.
 * The description is a human-readable string that describes the instruction.
 */
public enum InstructionType {

    NOP("No Operation", 0, new Opcode(1, (byte) 0x00)),
    DEBUG_DISPLAY("Debug Display", 0, new Opcode(1, (byte) 0xFF)),
    DEBUG_CONSOLE("Debug Console", 0, new Opcode(1, (byte) 0xFE)),
    LOAD_REGISTER("Load Register", 0, new SingleOpcode((byte) 0b01000000, 2, 6, 0)),
    LOAD_REGISTER_IMMEDIATE("Load Register Immediate", 1, new SingleOpcode((byte) 0b00000110, 2, 3, 3)),
    LOAD_REGISTER_ADDRESS("Load Register From Address in HL", 0, new SingleOpcode((byte) 0b00000010, 2, 3, 3)),;

    private final String description;
    private final int operandCount;
    private final Opcode opcode;

    /**
     * Create a new instruction type with the given description, operand count
     * and opcode.
     * 
     * @param description  The description of the instruction.
     * @param operandCount The number of operands the instruction has.
     * @param opcode       The opcode of the instruction.
     */
    InstructionType(String description, int operandCount, Opcode opcode) {
        this.description = description;
        this.operandCount = operandCount;
        this.opcode = opcode;
    }

    /**
     * Get the opcode of the instruction.
     * 
     * @return The opcode of the instruction.
     */
    public byte[] getOpcode() {
        return opcode.getOpcode();
    }

    /**
     * Get the description of the instruction.
     * 
     * @return The description of the instruction.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the number of operands the instruction has.
     * 
     * @return The number of operands the instruction has.
     */
    public int getOperandCount() {
        return operandCount;
    }

    // Return the first n bytes of the opcode
    private static byte[] firstN(byte[] opcode, int n) {
        byte[] result = new byte[n];
        for (int i = 0; i < n; i++) {
            result[i] = opcode[i];
        }
        return result;
    }

    /**
     * Get the instruction type from the given opcode.
     * 
     * @param opcode The opcode to get the instruction type from.
     * @return The instruction type.
     */
    public static InstructionType fromOpcode(byte[] opcode) {
        for (InstructionType type : values()) {
            for (int i = 0; i < opcode.length; i++) {
                if (type.opcode.matches(firstN(opcode, i + 1))) {
                    return type;
                }
            }
        }
        return null;
    }
}
