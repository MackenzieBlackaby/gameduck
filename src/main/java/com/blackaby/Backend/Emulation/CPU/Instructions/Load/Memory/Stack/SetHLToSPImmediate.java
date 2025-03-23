package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory.Stack;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class SetHLToSPImmediate extends Instruction {
    public SetHLToSPImmediate(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 3);
    }

    @Override
    public void run() {
        int sp = cpu.getSP();
        int offset = (byte) operands[0];
        int value = sp + offset;
        cpu.setHL(value);
        cpu.deactivateFlags(Flag.Z, Flag.N);
        cpu.setFlag(Flag.H, (sp & 0xF) + (offset & 0xF) > 0xF);
        cpu.setFlag(Flag.C, (sp & 0xFF) + (offset & 0xFF) > 0xFF);
    }

}
