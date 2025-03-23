package com.blackaby.Backend.Emulation.CPU.Instructions.Bits;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class SwapNibbles extends Instruction {

    private boolean isRegister;

    public SwapNibbles(DuckCPU cpu, DuckMemory memory, boolean isHL, boolean isRegister) {
        super(cpu, memory, isRegister ? 2 : 4);
        this.isRegister = isRegister;
    }

    @Override
    public void run() {
        int value = isRegister ? cpu.regGet(Register.getRegFrom3Bit(opcodeValues[0]))
                : memory.read(cpu.getHLValue());
        int result = ((value & 0x0F) << 4) | ((value & 0xF0) >> 4);
        result &= 0xFF;
        if (isRegister) {
            cpu.regSet(Register.getRegFrom3Bit(opcodeValues[0]), result);
        } else {
            memory.write(cpu.getHLValue(), result);
        }
        cpu.setFlag(Flag.Z, result == 0);
        cpu.deactivateFlags(Flag.N, Flag.H, Flag.C);

    }
}
