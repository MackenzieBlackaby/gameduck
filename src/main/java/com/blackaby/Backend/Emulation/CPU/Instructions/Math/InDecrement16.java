package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class InDecrement16 extends Instruction {

    private boolean increment;

    public InDecrement16(DuckCPU cpu, DuckMemory memory,
            boolean increment) {
        super(cpu, memory, 2);
        this.increment = increment;
    }

    @Override
    public void run() {
        Register source = Register.getRegFrom2Bit(opcodeValues[0], false);
        int value = cpu.regGet16(source);
        value = (value + (increment ? 1 : -1)) & 0xFFFF;
        cpu.regSet16(source, value);
    }
}
