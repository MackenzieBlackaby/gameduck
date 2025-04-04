package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory.Stack;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class StackPop extends Instruction {
    public StackPop(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 3);
    }

    @Override
    public void run() {
        int sp = cpu.getSP();
        // DebugLogger.logn("SP: " + sp);
        Register register = Register.getRegFrom2Bit(opcodeValues[0], true);

        int lsb = memory.stackPop(sp);
        if (register == Register.AF)
            lsb &= 0xF0;

        sp += 1;
        int msb = memory.stackPop(sp);
        sp += 1;

        int value = ((msb & 0xFF) << 8) | (lsb & 0xFF);

        cpu.regSet16(register, value);
        cpu.setSP(sp);
    }

}
