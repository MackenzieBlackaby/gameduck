package com.blackaby.Backend.Emulation.Misc;

/**
 * This class represents a binary instruction
 * It has an opcode, length, and operands
 * The opcode is the byte that represents the instruction in memory
 * The length is the number of bytes the instruction takes up in memory
 * The operands are the arguments of the instruction
 * The operands are stored as an array of integers
 */
public class BinaryInstruction {
    private byte opcode[];
    private byte operands[];

    /**
     * Constructor for BinaryInstruction
     * 
     * @param opcode   The opcode of the instruction
     * @param operands The operands of the instruction
     */
    public BinaryInstruction(byte opcode[], byte[] operands) {
        this.opcode = opcode;
        this.operands = operands;
    }

    /**
     * This method returns the opcode of the instruction
     * 
     * @return The opcode of the instruction
     */
    public byte[] getOpcode() {
        return opcode;
    }

    /**
     * This method returns the length of the instruction
     * 
     * @return The length of the instruction
     */
    public int getLength() {
        return opcode.length + operands.length;
    }

    /**
     * This method returns the operands of the instruction
     * 
     * @return The operands of the instruction
     */
    public byte[] getOperands() {
        return operands;
    }

    /**
     * This method returns the operand at the given index
     * 
     * @param index The index of the operand
     * @return The operand at the given index
     */
    public byte getOperand(int index) {
        return operands[index];
    }

    /**
     * This method returns a string representation of the instruction
     * 
     * @return A string representation of the instruction
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Opcode: ");
        for (byte b : opcode) {
            sb.append(String.format("%02X ", b));
        }
        sb.append("\nOperands: ");
        for (byte b : operands) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
