package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class AddPairHL extends Instruction {
    public AddPairHL(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int hl = cpu.getHLValue();
        Register reg = Register.getRegFrom2Bit(opcodeValues[0], false);
        int value = cpu.regGet16(reg);
        cpu.setHL(hl + value);

        cpu.setFlag(Flag.N, false);
        cpu.setFlag(Flag.H, ((hl & 0x0FFF) + (value & 0x0FFF)) > 0x0FFF);
        cpu.setFlag(Flag.C, ((hl & 0xFFFF) + (value & 0xFFFF)) > 0xFFFF);
    }
}