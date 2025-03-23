package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class CPL extends Instruction {

    public CPL(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        int value = ~cpu.getAccumulator() & 0xFF;
        cpu.setAccumulator(value);
        cpu.setFlag(DuckCPU.Flag.N, true);
        cpu.setFlag(DuckCPU.Flag.H, true);
    }
}
