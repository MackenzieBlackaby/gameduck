package com.blackaby.Backend.Emulation.CPU.Instructions.Load.CPU;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class ImmediateRegisterPair extends Instruction {
    public ImmediateRegisterPair(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 3);
    }

    @Override
    public void run() {
        int value = 0xFFFF & (((operands[1] & 0xFF) << 8) | (operands[0] & 0xFF));
        Register destination = Register.getRegFrom2Bit(opcodeValues[0], false);
        cpu.regSet16(destination, value);
    }
}
