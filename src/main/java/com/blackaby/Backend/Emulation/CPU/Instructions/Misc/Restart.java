package com.blackaby.Backend.Emulation.CPU.Instructions.Misc;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class Restart extends Instruction {

    public enum RestartType {
        RST00(0x00),
        RST08(0x08),
        RST10(0x10),
        RST18(0x18),
        RST20(0x20),
        RST28(0x28),
        RST30(0x30),
        RST38(0x38);

        private final int address;

        RestartType(int address) {
            this.address = address;
        }

        public int getAddress() {
            return address;
        }
    }

    public Restart(DuckCPU cpu, DuckMemory memory) {
        super(cpu, memory, 4);
    }

    @Override
    public void run() {
        int pc = cpu.getPC() + 1;
        int sp = cpu.getSP();
        memory.stackPush(sp, ((pc >> 8) & 0xFF));
        memory.stackPush(sp - 1, pc & 0xFF);
        RestartType type = RestartType.values()[opcodeValues[0]];
        cpu.setPC(type.getAddress());
        cpu.setSP(sp - 2);
    }
}
