package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

/**
 * Implements the ADD SP, e instruction.
 * 
 * Adds a signed 8-bit immediate value to the stack pointer (SP),
 * storing the result back in SP and setting flags accordingly.
 */
public class AddByteSP extends Instruction {
    /**
     * Constructs the instruction to add an immediate signed byte to SP.
     *
     * @param cpu    Reference to the DuckCPU instance
     * @param memory Reference to memory
     */
    public AddByteSP(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 4);
    }

    /**
     * Executes the instruction.
     * 
     * Adds a signed 8-bit immediate value to SP.
     * 
     * Flags:
     * - Z: Cleared
     * - N: Cleared
     * - H: Set if lower nibble overflow occurs
     * - C: Set if lower byte overflow occurs
     */
    @Override
    public void run() {
        int sp = cpu.getSP();
        int offset = (byte) operands[0];

        int spLow = sp & 0xFF;
        int offsetLow = offset & 0xFF;

        boolean halfCarry = ((spLow & 0x0F) + (offsetLow & 0x0F)) > 0x0F;
        boolean carry = (spLow + offsetLow) > 0xFF;

        int result = sp + offset;
        cpu.setSP(result & 0xFFFF);

        cpu.deactivateFlags(Flag.Z, Flag.N);
        cpu.setFlag(Flag.H, halfCarry);
        cpu.setFlag(Flag.C, carry);

    }
}