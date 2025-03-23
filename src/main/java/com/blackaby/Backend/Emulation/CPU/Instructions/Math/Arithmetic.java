package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;

public class Arithmetic extends Instruction {

    private ArithmeticType arithmeticType;
    private ValueType valueType;
    private boolean carry;

    public enum ArithmeticType {
        ADD, SUBTRACT, CP;
    }

    public Arithmetic(DuckCPU cpu, DuckMemory memory, ArithmeticType aType, ValueType valueType, boolean carry) {
        super(cpu, memory, valueType == ValueType.REGISTER ? 1 : 2);
        this.arithmeticType = aType;
        this.valueType = valueType;
        this.carry = carry;
    }

    @Override
    public void run() {
        int value = 0;
        Register sourceReg = null; // To track the source register if applicable
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

        int result = 0;
        int accumulator = cpu.getAccumulator();
        int carryValue = cpu.getFlagBoolean(DuckCPU.Flag.C) && carry ? 1 : 0;
        int effectiveValue = 0;

        switch (arithmeticType) {
            case ADD: {
                effectiveValue = value + carryValue;
                result = accumulator + effectiveValue;
                break;
            }
            case SUBTRACT: {
                effectiveValue = value + carryValue;
                result = accumulator - effectiveValue;
                break;
            }
            case CP: {
                effectiveValue = value;
                result = accumulator - value;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid arithmetic type");
            }
        }

        int oldResult = result;
        result &= 0xFF;

        if (arithmeticType != ArithmeticType.CP)
            cpu.setAccumulator(result);

        cpu.setFlag(Flag.Z, result == 0);
        cpu.setFlag(Flag.N, arithmeticType != ArithmeticType.ADD);
        cpu.setFlag(Flag.H, ((accumulator ^ effectiveValue ^ result) & 0x10) != 0);
        if (arithmeticType == ArithmeticType.ADD) {
            cpu.setFlag(Flag.C, oldResult > 0xFF);
        } else {
            cpu.setFlag(Flag.C, oldResult < 0);
        }

    }

}
