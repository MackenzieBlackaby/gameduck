package com.blackaby.Backend.Emulation.CPU.Instructions.Bits;

import com.blackaby.Backend.Emulation.CPU.Instruction;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Register;
import com.blackaby.Backend.Emulation.CPU.DuckCPU.Flag;

public class Rotate extends Instruction {

    private boolean left;
    private boolean accumulator;
    private boolean hl;
    private boolean circle; // true = circular rotate; false = rotate through carry
    private boolean forceZ;

    // Constructor: cycle count is initially set to 2, but adjusted in run()
    // For accumulator rotates, cycle count will be 1; for HL rotates, 4; for
    // register rotates, 2.
    public Rotate(DuckCPU cpu, DuckMemory memory, boolean left, boolean circle, boolean accumulator, boolean hl, boolean isCBPrefix) {
        super(cpu, memory, 2);
        this.left = left;
        this.circle = circle;
        this.accumulator = accumulator;
        this.hl = hl;
        this.forceZ = !isCBPrefix;
        if (accumulator)
            cycles = 1;
        else if (hl)
            cycles = 4;
    }

    @Override
    public void run() {
        int value;
        if (accumulator) {
            value = cpu.getAccumulator();
        } else if (hl) {
            value = memory.read(cpu.getHLValue());
        } else {
            value = cpu.regGet(Register.getRegFrom3Bit(opcodeValues[0]));
        }

        // We'll use two different branches: one for circular rotate and one for
        // through-carry.
        if (circle) {
            // Circular rotate: the shifted-out bit is reinserted.
            boolean shiftedOut;
            if (left) {
                shiftedOut = (value & 0x80) != 0; // extract bit 7
                value = (value << 1) & 0xFF;
                if (shiftedOut) {
                    value |= 0x01;
                }
            } else {
                shiftedOut = (value & 0x01) != 0; // extract bit 0
                value = (value >>> 1) & 0xFF;
                if (shiftedOut) {
                    value |= 0x80;
                }
            }
            // Update carry flag with the bit that was shifted out.
            cpu.setFlag(Flag.C, shiftedOut);
        } else {
            // Rotate through carry: insert the old carry flag into the new bit.
            boolean oldCarry = cpu.getFlagBoolean(Flag.C);
            int shiftedOut;
            if (left) {
                shiftedOut = (value & 0x80) != 0 ? 1 : 0;
                value = (value << 1) & 0xFF;
                if (oldCarry) {
                    value |= 0x01;
                }
            } else {
                shiftedOut = (value & 0x01) != 0 ? 1 : 0;
                value = (value >>> 1) & 0xFF;
                if (oldCarry) {
                    value |= 0x80;
                }
            }
            // Update carry flag with the shifted-out bit.
            cpu.setFlag(Flag.C, shiftedOut == 1);
        }

        // Write the result back to the appropriate destination.
        if (accumulator) {
            cpu.setAccumulator(value);
        } else if (hl) {
            memory.write(cpu.getHLValue(), value);
        } else {
            cpu.regSet(Register.getRegFrom3Bit(opcodeValues[0]), value);
        }
        // Set the zero flag if the result is zero and affectZ is true.
        if (forceZ)
            cpu.setFlag(Flag.Z, false);
        else
            cpu.setFlag(Flag.Z, value == 0);
        // Clear the H and N flags.
        cpu.deactivateFlags(Flag.H, Flag.N);
    }
}
