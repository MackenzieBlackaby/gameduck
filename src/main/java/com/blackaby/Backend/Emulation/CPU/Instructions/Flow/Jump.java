package com.blackaby.Backend.Emulation.CPU.Instructions.Flow;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Jump extends Instruction {

    private boolean conditional;
    private boolean isRelative;
    private boolean isHL;

    // Absolute jumps:
    // Unconditional or successful conditional jumps: 4 cycles
    // Unsuccessful conditional jumps: 3 cycles
    // Relative jumps:
    // Unconditional or successful conditional jumps: 3 cycles
    // Unsuccessful conditional jumps: 2 cycles
    public Jump(DuckCPU cpu, DuckMemory memory, boolean conditional, boolean isRelative, boolean isHL) {
        super(cpu, memory, 1);
        this.conditional = conditional;
        this.isRelative = isRelative;
        this.isHL = isHL;
    }

    @Override
    public void run() {
        int pc = cpu.getPC();
        // DebugLogger.logn("Executing Jump:");
        // DebugLogger.logn("Current PC: " + Integer.toHexString(pc));

        // Determine cycles based on jump type and condition
        if (conditional) {
            int condCode = opcodeValues[0] & 0b11;
            boolean jumpConditionMet = false;
            switch (condCode) {
                case 0b00: // Z flag set
                    // DebugLogger.logn("BRNZ");
                    // DebugLogger.logn("Z flag as bool: " + cpu.getFlagBoolean(Flag.Z));
                    jumpConditionMet = !cpu.getFlagBoolean(Flag.Z);
                    break;
                case 0b01: // Z flag clear
                    // DebugLogger.logn("BRZ");
                    // DebugLogger.logn("Z flag as bool: " + cpu.getFlagBoolean(Flag.Z));
                    jumpConditionMet = cpu.getFlagBoolean(Flag.Z);
                    break;
                case 0b10: // C flag set
                    // DebugLogger.logn("BRNC");
                    // DebugLogger.logn("C flag as bool: " + cpu.getFlagBoolean(Flag.C));
                    jumpConditionMet = !cpu.getFlagBoolean(Flag.C);
                    break;
                case 0b11: // C flag clear
                    // DebugLogger.logn("BRC");
                    // DebugLogger.logn("C flag as bool: " + cpu.getFlagBoolean(Flag.C));
                    jumpConditionMet = cpu.getFlagBoolean(Flag.C);
                    break;
            }
            if (!jumpConditionMet) {
                // Condition not met: use unsuccessful cycle count.
                if (isRelative) {
                    cycles = 2; // Relative conditional not taken: 2 cycles
                } else {
                    cycles = 3; // Absolute conditional not taken: 3 cycles
                }
                // DebugLogger.logn("Conditional jump not taken; PC remains " +
                // Integer.toHexString(pc));
                return;
            } else {
                // Condition met: use the longer timing.
                if (isRelative) {
                    cycles = 3; // Relative conditional taken: 3 cycles
                } else {
                    cycles = 4; // Absolute conditional taken: 4 cycles
                }
            }
        } else {
            // Unconditional jump
            if (isHL) {
                cycles = 1; // HL jumps always 1 cycle
            } else if (isRelative) {
                cycles = 3; // Unconditional relative: 3 cycles
            } else {
                cycles = 4; // Unconditional absolute: 4 cycles
            }
        }

        // Now perform the jump.
        if (isRelative) {
            int offset = (byte) operands[0];
            int newPC = pc + offset;
            // DebugLogger.logn("Relative Jump: Offset = " + Integer.toHexString(offset) +
            // ", New PC = " + Integer.toHexString(newPC));
            cpu.setPC(newPC);
        } else if (isHL) {
            int hlValue = cpu.getHLValue();
            // DebugLogger.logn("Jump to HL: HL Value = " + Integer.toHexString(hlValue));
            cpu.setPC(hlValue);
        } else {
            // For absolute jumps, operand bytes are stored in values[1] (low) and values[2]
            // (high)
            int target = ((operands[1] & 0xFF) << 8) | (operands[0] & 0xFF);
            // DebugLogger.logn("Absolute Jump: Target Address = " +
            // Integer.toHexString(target));
            cpu.setPC(target);
        }
    }

}
