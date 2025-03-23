package com.blackaby.Backend.Emulation.CPU.Instructions.Misc;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Nop extends Instruction {

    public Nop(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        // Do nothing
    }

}
