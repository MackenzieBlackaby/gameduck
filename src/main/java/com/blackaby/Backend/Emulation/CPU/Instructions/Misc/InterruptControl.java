package com.blackaby.Backend.Emulation.CPU.Instructions.Misc;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class InterruptControl extends Instruction {

    boolean enable;

    public InterruptControl(DuckCPU cpu, DuckMemory memory, boolean enable) {
        super(cpu, memory, 1);
        this.enable = enable;
    }

    @Override
    public void run() {
        cpu.setInterruptEnable(enable);
    }

}
