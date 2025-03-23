package com.blackaby.Backend.Emulation.CPU.Instructions.Load.CPU;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class LoadToRegister extends Instruction {

    private boolean sourceIsImmediate;

    public LoadToRegister(DuckCPU cpu, DuckMemory memory, boolean sourceIsImmediate) {
        super(cpu, memory, sourceIsImmediate ? 2 : 1);
        this.sourceIsImmediate = sourceIsImmediate;
    }

    @Override
    public void run() {
        int value;
        Register destination;
        destination = Register.getRegFrom3Bit(opcodeValues[0]);
        if (sourceIsImmediate) {
            value = operands[0] & 0xFF;
        } else {
            Register source = Register.getRegFrom3Bit(opcodeValues[1]);
            value = cpu.regGet(source);
        }
        cpu.regSet(destination, value);
    }

}
