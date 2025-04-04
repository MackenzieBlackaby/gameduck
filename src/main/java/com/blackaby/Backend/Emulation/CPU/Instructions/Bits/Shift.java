package com.blackaby.Backend.Emulation.CPU.Instructions.Bits;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Shift extends Instruction {

    boolean left;
    boolean arithmetic;
    boolean register;

    // Register is 2, hl is 4,
    public Shift(DuckCPU cpu, DuckMemory memory, boolean left, boolean arithmetic, boolean register) {
        super(cpu, memory, register ? 2 : 4);
        this.left = left;
        this.arithmetic = arithmetic;
        this.register = register;
    }

    @Override
    public void run() {
        int value;
        Register reg = null;
        if (register) {
            reg = Register.getRegFrom3Bit(opcodeValues[0]);
            value = cpu.regGet(reg);
        } else {
            value = memory.read(cpu.getHLValue());
        }
        boolean carry = left ? (value & 0x80) != 0 : (value & 0x01) != 0;

        if (left) {
            value = (value << 1) & 0xFF;
        } else {
            if (arithmetic) {
                boolean bit7 = (value & 0x80) != 0;
                value >>= 1;
                if (bit7) {
                    value |= 0x80;
                }
            } else {
                value >>>= 1;
            }
            value = (value & 0xFF);
        }

        if (register) {
            cpu.regSet(reg, value);
        } else {
            memory.write(cpu.getHLValue(), value);
        }

        cpu.setFlag(DuckCPU.Flag.C, carry);
        cpu.setFlag(DuckCPU.Flag.Z, value == 0);
        cpu.deactivateFlags(DuckCPU.Flag.H, DuckCPU.Flag.N);
    }
}