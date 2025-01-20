package com.blackaby.Backend.Emulation.CPU;

import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;

/**
 * This class represents the Increment/Decrement Unit of the CPU
 * It contains methods for performing increment and decrement operations on the
 * CPU
 */
public class DuckIDU {
    private DuckCPU cpu;

    /**
     * Creates a new DuckIDU
     * 
     * @param boundCPU the CPU to use
     */
    public DuckIDU(DuckCPU boundCPU) {
        this.cpu = boundCPU;
    }

    public byte calculate(byte value, boolean isIncrement) {
        cpu.setFlag(Flag.Z, value == 0);
        cpu.setFlag(Flag.N, false);
        cpu.setFlag(Flag.H, ((value) & 0xF) - ((isIncrement ? 1 : -1) & 0xF) < 0);

        return (byte) (value + (isIncrement ? 1 : -1));
    }
}
