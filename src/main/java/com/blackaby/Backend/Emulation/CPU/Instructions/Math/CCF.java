package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class CCF extends Instruction {

    public CCF(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        boolean carry = cpu.getFlagBoolean(DuckCPU.Flag.C);
        cpu.setFlag(DuckCPU.Flag.C, !carry);
        cpu.setFlag(DuckCPU.Flag.N, false);
        cpu.setFlag(DuckCPU.Flag.H, false);
    }
}
