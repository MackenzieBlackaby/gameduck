package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory.Stack;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class StackPush extends Instruction {
    public StackPush(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 4);
    }

    @Override
    public void run() {
        int sp = cpu.getSP();
        Register register = Register.getRegFrom2Bit(opcodeValues[0], true);
        int value = cpu.regGet16(register) & 0xFFFF;
        if (register == Register.AF)
            value &= 0xFFF0;
        int msb = (value >> 8) & 0xFF;
        int lsb = value & 0xFF;

        sp--; // Step 1: Decrement SP by 1
        memory.stackPush(sp, msb); // Store LSB at new SP

        sp--; // Step 2: Decrement SP by 1 again
        memory.stackPush(sp, lsb); // Store MSB at new SP

        cpu.setSP(sp); // Update SP

    }

}
