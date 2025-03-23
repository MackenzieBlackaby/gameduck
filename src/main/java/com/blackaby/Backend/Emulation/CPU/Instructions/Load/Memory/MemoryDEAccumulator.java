package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class MemoryDEAccumulator extends Instruction {
    public MemoryDEAccumulator(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int address = cpu.getDEValue();
        int value = memory.read(address);
        cpu.setAccumulator(value);
    }
}
