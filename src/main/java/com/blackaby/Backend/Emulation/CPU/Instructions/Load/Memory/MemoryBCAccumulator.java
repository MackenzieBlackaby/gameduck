package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class MemoryBCAccumulator extends Instruction {
    public MemoryBCAccumulator(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int address = cpu.getBCValue();
        int value = memory.read(address);
        cpu.setAccumulator(value);
    }
}
