package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class AccumulatorMemoryMaskC extends Instruction {
    public AccumulatorMemoryMaskC(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int address = 0xFF00 | cpu.getC();
        int value = cpu.getAccumulator();
        memory.write(address, value);
    }
}