package com.blackaby.Backend.Emulation.CPU.Instructions.Flow;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;

public class Return extends Instruction {

    boolean conditional;
    boolean interrupt;

    // Return unconditional: 4 cycles
    // Return conditional: 5 cycles in success, 2 cycles in fail
    // Return from interrupt: 4 cycles
    public Return(DuckCPU cpu, DuckMemory memory, boolean conditional, boolean interrupt) {
        super(cpu, memory, conditional ? 5 : 4);
        this.conditional = conditional;
        this.interrupt = interrupt;
    }

    @Override
    public void run() {
        if (conditional) {
            int condition = opcodeValues[0] & 0b11;
            switch (condition) {
                case 0b00: {
                    if (cpu.getFlagBoolean(Flag.Z)) {
                        cycles -= 3;
                        return;
                    }
                    break;
                }
                case 0b01: {
                    if (!cpu.getFlagBoolean(Flag.Z)) {
                        cycles -= 3;
                        return;
                    }
                    break;
                }
                case 0b10: {
                    if (cpu.getFlagBoolean(Flag.C)) {
                        cycles -= 3;
                        return;
                    }
                    break;
                }
                case 0b11: {
                    if (!cpu.getFlagBoolean(Flag.C)) {
                        cycles -= 3;
                        return;
                    }
                    break;
                }
            }
        }
        int sp = cpu.getSP();
        int low = memory.stackPop(sp); // high is at SP
        sp++;
        int high = memory.stackPop(sp); // low is at SP+1
        sp++;
        cpu.setSP(sp);
        int pc = (high << 8) | low;
        cpu.setPC(pc);
        if (interrupt) {
            cpu.setInterruptMasterEnable(true);
        }
    }
}
