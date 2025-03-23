package com.blackaby.Backend.Emulation.CPU.Instructions.Flow;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;

public class Call extends Instruction {

    boolean conditional;

    // Call unconditional 6 cycles
    // Call conditional 3 cycles if fail, 6 cycles if success
    public Call(DuckCPU cpu, DuckMemory memory, boolean conditional) {
        super(cpu, memory, 3);
        this.conditional = conditional;
    }

    @Override
    public void run() {
        // DebugLogger.logn("Executing CALL instruction...");

        // Handle conditional call
        if (conditional) {
            int condition = opcodeValues[0] & 0b11;
            switch (condition) {
                case 0b00: {
                    if (cpu.getFlagBoolean(Flag.Z)) {
                        return;
                    }
                    break;
                }
                case 0b01: {
                    if (!cpu.getFlagBoolean(Flag.Z)) {
                        return;
                    }
                    break;
                }
                case 0b10: {
                    if (cpu.getFlagBoolean(Flag.C)) {
                        return;
                    }
                    break;
                }
                case 0b11: {
                    if (!cpu.getFlagBoolean(Flag.C)) {
                        return;
                    }
                    break;
                }
            }
        }

        cycles += 3;

        // Extract the target address from instruction operands
        int address = (operands[1] << 8) | (operands[0] & 0xFF);
        // DebugLogger.logn("CALL target address: " + String.format("0x%04X",
        // address));

        // Save the current PC onto the stack
        int sp = cpu.getSP();
        int returnAddress = cpu.getPC();
        // DebugLogger.logn("Current SP: " + String.format("0x%04X", sp));
        // DebugLogger.logn("Pushing return address onto stack: " +
        // String.format("0x%04X", returnAddress));

        sp--;
        memory.stackPush(sp, (returnAddress >> 8) & 0xFF);
        sp--;
        memory.stackPush(sp, returnAddress & 0xFF);
        cpu.setSP(sp);
        // DebugLogger.logn("Updated SP after push: " + String.format("0x%04X",
        // cpu.getSP()));

        // Set PC to the new address
        cpu.setPC(address);
    }
}
