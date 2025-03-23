package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class SCF extends Instruction {

    public SCF(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        cpu.setFlag(DuckCPU.Flag.C, true);
        cpu.setFlag(DuckCPU.Flag.N, false);
        cpu.setFlag(DuckCPU.Flag.H, false);
    }
}
