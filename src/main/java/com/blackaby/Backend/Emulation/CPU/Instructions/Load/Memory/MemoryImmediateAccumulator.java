package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class MemoryImmediateAccumulator extends Instruction {
    public MemoryImmediateAccumulator(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 4);
    }

    @Override
    public void run() {
        int address = 0xFFFF & ((operands[1] << 8) | (operands[0] & 0xFF));
        int value = memory.read(address);
        cpu.setAccumulator(value);
    }
}
