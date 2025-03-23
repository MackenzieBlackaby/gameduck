package com.blackaby.Backend.Emulation.CPU.Instructions.Math;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class DAA extends Instruction {

    public DAA(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 1);
    }

    @Override
    public void run() {
        int a = cpu.getAccumulator();
        boolean n = cpu.getFlagBoolean(DuckCPU.Flag.N);
        boolean c = cpu.getFlagBoolean(DuckCPU.Flag.C);
        boolean h = cpu.getFlagBoolean(DuckCPU.Flag.H);
        int correction = 0;

        if (n) { // Subtraction
            if (c) {
                correction += 0x60;
            }
            if (h) {
                correction += 0x06;
            }

            a = (a - correction) & 0xFF;
        } else { // Addition
            if (c || a > 0x99) {
                correction += 0x60;
                c = true;
            } else {
                c = false;
            }
            if (h || (a & 0x0F) > 0x09) {
                correction += 0x06;
            }
            a = (a + correction) & 0xFF;
        }

        cpu.setAccumulator(a);
        cpu.setFlag(DuckCPU.Flag.C, c);
        cpu.setFlag(DuckCPU.Flag.H, false);
        cpu.setFlag(DuckCPU.Flag.Z, a == 0);
    }
}
