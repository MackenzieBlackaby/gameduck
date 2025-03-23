package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class MemoryAccumulatorMaskC extends Instruction {
    public MemoryAccumulatorMaskC(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int c = cpu.getC();
        int address = 0xFF00 | (c & 0xFF);
        int value = memory.read(address);
        cpu.setAccumulator(value);
        
    }
}