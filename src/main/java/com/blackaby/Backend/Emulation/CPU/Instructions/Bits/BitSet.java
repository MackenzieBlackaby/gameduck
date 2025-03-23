package com.blackaby.Backend.Emulation.CPU.Instructions.Bits;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class BitSet extends Instruction {

    private boolean isRegister;
    private boolean activate;
    private boolean test;

    // Register = 2, hl test = 3, hl = 4
    public BitSet(DuckCPU cpu, DuckMemory memory, boolean isActive, boolean isRegister, boolean isTest) {
        super(cpu, memory, isRegister ? 2 : 4);
        if (isTest)
            cycles = 3;
        this.isRegister = isRegister;
        this.activate = isActive;
        this.test = isTest;
    }

    @Override
    public void run() {
        int value = 0;
        Register reg = Register.A;
        if (isRegister) {
            reg = Register.getRegFrom3Bit(opcodeValues[1]);
            value = cpu.regGet(reg);
        } else {
            value = memory.read(cpu.getHLValue());
        }
        int bitPos = opcodeValues[0] & 0b111; // Extract the bit position (0-7)
        int mask = 0xFF & (1 << bitPos); // Create the bitmask (only a single bit)

        if (test) {
            // Test the bit, set flags accordingly
            boolean isSet = (value & mask) != 0; // Check if the bit is set
            cpu.setFlag(Flag.Z, !isSet); // Set Z flag if the bit is not set
            cpu.setFlag(Flag.H, true); // Set H flag (for half-carry)
            cpu.deactivateFlags(Flag.N); // Deactivate N flag (for subtraction)
        } else {
            // Set or clear the bit in the value
            if (activate) {
                value |= mask; // Set the bit
            } else {
                value &= ~mask; // Clear the bit
            }

            if (isRegister) {
                cpu.regSet(reg, (byte) value); // Set the register value
            } else {
                memory.write(cpu.getHLValue(), (byte) value); // Write to memory
            }
        }

        // Debugging print statement (can be replaced with a logger if needed)
        // DebugLogger.logn("Bit " + bitPos + " of " + (isRegister ? reg : "HL") + "
        // set to " + activate);
        // DebugLogger.logn("Updated value: " + Integer.toBinaryString(value & 0xFF));
        // Print the updated value in
        // binary format
    }
}
