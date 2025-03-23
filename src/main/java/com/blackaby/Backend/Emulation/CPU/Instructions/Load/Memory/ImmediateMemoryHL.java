package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class ImmediateMemoryHL extends Instruction {

    public ImmediateMemoryHL(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 3);
    }

    @Override
    public void run() {
        int address = cpu.getHLValue();
        int value = operands[0] & 0xFF;
        memory.write(address, value);
    }
}
