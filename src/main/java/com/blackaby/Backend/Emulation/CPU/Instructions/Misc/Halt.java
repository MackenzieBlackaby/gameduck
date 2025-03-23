package com.blackaby.Backend.Emulation.CPU.Instructions.Misc;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Halt extends Instruction {
    public Halt(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        boolean interruptPending = (memory.read(DuckMemory.IE) & memory.read(DuckMemory.INTERRUPT_FLAG) & 0x1F) != 0;

        if (!cpu.isInterruptMasterEnable() && interruptPending) {
            // IME is disabled and an interrupt is pending: HALT bug!
            cpu.setHaltBug(true);
        } else {
            // Normal HALT: CPU stops execution until an interrupt occurs
            cpu.setHalted(true);
        }
    }
}
