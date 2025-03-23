package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class AddByteSP extends Instruction {
    public AddByteSP(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 4);
    }

    @Override
    public void run() {
        int sp = cpu.getSP();
        int offset = (byte) operands[0]; // -128..+127

        // For the 8-bit carry checks, mask offset to 0..255
        int spLow = sp & 0xFF;
        int offsetLow = offset & 0xFF; // ignoring sign here

        boolean halfCarry = ((spLow & 0x0F) + (offsetLow & 0x0F)) > 0x0F;
        boolean carry = (spLow + offsetLow) > 0xFF;

        // Now do the signed 16-bit addition for SP
        int result = sp + offset; // This is the real SP update
        cpu.setSP(result & 0xFFFF); // typically mask to 16 bits

        cpu.deactivateFlags(Flag.Z, Flag.N);
        cpu.setFlag(Flag.H, halfCarry);
        cpu.setFlag(Flag.C, carry);

    }
}