package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Frontend.DebugLogger;

public class MemoryHLAccumulatorChange extends Instruction {

    private boolean increment;

    public MemoryHLAccumulatorChange(DuckCPU cpu, DuckMemory memory, boolean increment) {
        super(cpu, memory, 2);
        this.increment = increment;
    }

    @Override
    public void run() {
        int hlValue = cpu.getHLValue();
        int memoryValue = memory.read(hlValue);
        cpu.setAccumulator(memoryValue);
        int newHL = hlValue + (increment ? 1 : -1);
        cpu.setHL(newHL);
    }
}
