package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory.Stack;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class SetSPToHL extends Instruction {

    public SetSPToHL(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        int hl = 0xFFFF & cpu.getHLValue();
        cpu.setSP(hl);
    }
}