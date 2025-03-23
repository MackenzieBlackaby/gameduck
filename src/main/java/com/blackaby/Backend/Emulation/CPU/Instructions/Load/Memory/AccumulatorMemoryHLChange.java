package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;

public class AccumulatorMemoryHLChange extends Instruction {

    private boolean increment;

    public AccumulatorMemoryHLChange(DuckCPU cpu, DuckMemory memory, boolean increment) {
        super(cpu, memory, 2);
        this.increment = increment;
    }
    // TODO: AF and SP context changes
    @Override
    public void run() {
        int hlValue = cpu.getHLValue();
        int accumulator = cpu.getAccumulator();
        memory.write(hlValue, accumulator);
        int newHL = hlValue + (increment ? 1 : -1);
        cpu.setHL(newHL);
    }
}
