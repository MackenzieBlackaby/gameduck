package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory.Stack;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class ImmediateSPMemory extends Instruction {

    public ImmediateSPMemory(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 5);
    }

    @Override
    public void run() {
        int sp = cpu.getSP();
        int address = ((operands[1] & 0xFF) << 8) | (operands[0] & 0xFF);
        memory.write(address, sp & 0xFF); // Store LSB
        memory.write(address + 1, (sp >> 8) & 0xFF); // Store MSB
    }
}
