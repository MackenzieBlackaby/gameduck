package com.blackaby.Backend.Emulation.CPU.Instructions;

import com.blackaby.Backend.Emulation.DuckEmulation;

/**
 * This class manages the different types of instructions that the CPU can
 * execute
 * It contains an enum of all the different types of instructions, and a method
 * to get the type of an instruction
 * It also contains a method to construct an instruction from the given type,
 * opcode, and operands
 */
public class InstructionTypeManager {

    /**
     * This enum represents the different types of instructions that the CPU can
     * perform
     * Each instruction has an opcode string, for comparisons and value extraction,
     * and an operand count
     * The enum also contains various getters and a method to determine if the
     * instruction extracts values from the opcode
     */
    public enum InstructionType {
        // FROM_TO(_REGS/DETAILS)
        // Load instructions (ids 0 - 24)
        REGISTER_REGISTER("01aaabbb", 0),
        IMMEDIATE_REGISTER("00aaa110", 1),
        MEMORY_REGISTER_HL("01aaa110", 0),
        REGISTER_MEMORY_HL("0110aaa", 0),
        IMMEDIATE_MEMORY_HL("00110110", 1),
        MEMORY_ACCUMULATOR_BC("00001010", 0),
        MEMORY_ACCUMULATOR_DE("00011010", 0),
        ACCUMULATOR_MEMORY_BC("00000010", 0),
        ACCUMULATOR_MEMORY_DE("00010010", 0),
        MEMORY_ACCUMULATOR_IMMEDIATE("11111010", 2),
        ACCUMULATOR_MEMORY_IMMEDIATE("11101010", 2),
        MEMORY_ACCUMULATOR_MSB_0xFF_C("11110010", 0),
        ACCUMULATOR_MEMORY_MSB_0xFF_C("11100010", 0),
        MEMORY_ACCUMULATOR_MSB_0xFF_IMMEDIATE("11110000", 1),
        ACCUMULATOR_MEMORY_MSB_0xFF_IMMEDIATE("11100000", 1),
        MEMORY_ACCUMULATOR_HL_DECREMENT("00111010", 0),
        ACCUMULATOR_MEMORY_HL_DECREMENT("00110010", 0),
        MEMORY_ACCUMULATOR_HL_INCREMENT("00101010", 0),
        ACCUMULATOR_MEMORY_HL_INCREMENT("00100010", 0),
        IMMEDIATE_PAIR("00aa0001", 2),
        SP_MEMORY_IMMEDIATE("00001000", 2),
        HL_SP("11111001", 0),
        STACKPUSH_RR("11aa0101", 0),
        STACKPOP_RR("11aa0001", 0),
        SP_PLUS_IMMEDIATE8_HL("11111000", 1);
        // Arithmetic instructions (IDs 25 - x)

        private final String opcode;
        private final byte operandCount;

        /**
         * This array contains all the instruction types that extract values from the
         * opcode
         */
        public static final InstructionType[] OpcodeExtractingTypes = {
                REGISTER_REGISTER, IMMEDIATE_REGISTER, MEMORY_REGISTER_HL, REGISTER_MEMORY_HL, IMMEDIATE_PAIR,
                STACKPUSH_RR, STACKPOP_RR
        };

        /**
         * This constructor creates a new instruction type with the given opcode and
         * operand count
         * 
         * @param opcode       The opcode of the instruction
         * @param operandCount The number of operands the instruction has
         */
        InstructionType(String opcode, int operandCount) {
            this.opcode = opcode;
            this.operandCount = (byte) operandCount;
        }

        /**
         * This method returns the opcode of the instruction
         * 
         * @return The opcode of the instruction
         */
        public String getOpcode() {
            return opcode;
        }

        /**
         * This method returns the number of operands the instruction has
         * 
         * @return The number of operands the instruction has
         */
        public byte getOperandCount() {
            return operandCount;
        }

        /**
         * This method returns the ID/index of the instruction in the enum
         * 
         * @return The ID of the instruction in the enum
         */
        public int getID() {
            return ordinal();
        }

        /**
         * This method returns whether the instruction extracts values from the opcode
         * 
         * @return true if the instruction extracts values from the opcode
         */
        public boolean doesExtractOpcode() {
            for (InstructionType type : OpcodeExtractingTypes) {
                if (type == this)
                    return true;
            }
            return false;
        }
    }

    private static boolean compareOpcode(String base, byte opcode) {
        char array[] = base.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 'a' || array[i] == 'b')
                continue;
            boolean currentBit = (opcode & (1 << (7 - i))) != 0;
            if (array[i] == '0' && currentBit || array[i] == '1' && !currentBit)
                return false;
        }
        return true;
    }

    /**
     * This method returns the type of instruction that the given opcode represents
     * 
     * @param opcode The opcode of the instruction
     * @return The type of instruction that the opcode represents, or null if no
     *         type
     */
    public static InstructionType getType(byte opcode) {
        for (InstructionType instruction : InstructionType.values()) {
            if (compareOpcode(instruction.getOpcode(), opcode)) {
                return instruction;
            }
        }
        return null;
    }

    /**
     * This method constructs an instruction from the given type, opcode, and
     * operands
     * 
     * @param boundEmulation The emulation that the instruction is bound to
     * @param instruction    The type of instruction
     * @param opcode         The opcode of the instruction
     * @param operands       The operands of the instruction
     * @return The constructed instruction
     */
    public static Duckstruction constructInstruction(DuckEmulation boundEmulation, InstructionType instruction,
            byte opcode, byte... operands) {
        if (instruction.getID() < 25) {
            return new Duckstruction(boundEmulation.getCPU(), boundEmulation.getMemory(), instruction, opcode,
                    operands);
        }
        return null;
    }
}
