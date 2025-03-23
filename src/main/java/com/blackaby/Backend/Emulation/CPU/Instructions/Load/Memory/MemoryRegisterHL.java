package com.blackaby.Backend.Emulation.CPU.Instructions.Load.Memory;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class MemoryRegisterHL extends Instruction {

    public MemoryRegisterHL(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 2);
    }

    @Override
    public void run() {
        Register destination = Register.getRegFrom3Bit(opcodeValues[0]);
        int address = cpu.getHLValue();
        int value = memory.read(address);
        cpu.regSet(destination, value);
    }
}
