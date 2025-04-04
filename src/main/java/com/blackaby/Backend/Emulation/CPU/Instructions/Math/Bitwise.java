package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Bitwise extends Instruction {
    private ValueType valueType;
    private BitwiseType bitwiseType;

    public enum BitwiseType {
        AND, OR, XOR
    }

    public Bitwise(DuckCPU cpu, DuckMemory memory, ValueType valueType, BitwiseType bitwiseType) {
        super(cpu, memory, 2);
        this.valueType = valueType;
        this.bitwiseType = bitwiseType;
        if (valueType == ValueType.REGISTER)
            cycles = 1;
    }

    @Override
    public void run() {
        int value = 0;
        Register sourceReg = null;

        switch (valueType) {
            case REGISTER: {
                sourceReg = Register.getRegFrom3Bit(opcodeValues[0]);
                value = cpu.regGet(sourceReg);
                break;
            }
            case HL_MEMORY: {
                value = memory.read(cpu.getHLValue());
                break;
            }
            case IMMEDIATE: {
                value = operands[0];
                value &= 0xFF;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid arithmetic type");
            }
        }

        switch (bitwiseType) {
            case AND: {
                value &= cpu.regGet(Register.A);
                break;
            }
            case OR: {
                value |= cpu.regGet(Register.A);
                break;
            }
            case XOR: {
                value ^= cpu.regGet(Register.A);
                break;
            }
        }
        value &= 0xFF;
        cpu.regSet(Register.A, value);

        cpu.setFlag(Flag.Z, value == 0);
        cpu.setFlag(Flag.H, bitwiseType == BitwiseType.AND);
        cpu.deactivateFlags(Flag.C, Flag.N);
    }
}
