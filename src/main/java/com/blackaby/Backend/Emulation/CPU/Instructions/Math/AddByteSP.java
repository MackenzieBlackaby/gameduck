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