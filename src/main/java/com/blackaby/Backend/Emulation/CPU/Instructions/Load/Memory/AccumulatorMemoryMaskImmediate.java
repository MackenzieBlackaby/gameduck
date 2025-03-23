package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Frontend.DebugLogger;

public class AccumulatorMemoryMaskImmediate extends Instruction {
    public AccumulatorMemoryMaskImmediate(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 3);
    }

    @Override
    public void run() {
        int lsb = operands[0] & 0xFF;
        int address = 0xFF00 | (lsb & 0xFF);
        int value = cpu.getAccumulator();
        memory.write(address, value);
    }
}
