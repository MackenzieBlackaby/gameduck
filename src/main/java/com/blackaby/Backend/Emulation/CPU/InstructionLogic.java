package com.blackaby.Backend.Emulation.CPU;

import com.blackaby.Backend.Emulation.Graphics.DuckPPU;
import com.blackaby.Backend.Emulation.Memory.DuckAddresses;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

/**
 * This class handles the logic for different CPU instructions
 * Each instruction will return the T-cycle count for that specific instruction
 */
public class InstructionLogic {
    private static DuckCPU cpu;
    private static DuckMemory memory;
    private static DuckPPU ppu;

    /**
     * Stop Instruction
     * 
     * @return 4 T cycles
     */
    public static int Stop() {
        cpu.setStopped(true);
        return 4;
    }

    /**
     * Restart instruction
     * 
     * @return 16 T cycles
     */
    public static int Restart(int address) {
        int pc = cpu.getPC();
        memory.stackPushShort(pc);
        cpu.setPC(address & 0xFFFF);
        return 16;
    }

    /**
     * Nop instruction
     * 
     * @return 4 T cycles
     */
    public static int Nop() {
        return 4;
    }

    /**
     * Interrupt control
     * 
     * @return 4 T cycles
     */
    public static int InterruptControl(boolean enable) {
        if (enable)
            cpu.scheduleEnableInterrupts();
        else
            cpu.disableInterrupts();
        return 4;
    }

    /**
     * Halt instruction, with halt bug implemented
     * 
     * @return 4 T cycles
     */
    public static int Halt() {
        int ie = memory.read(DuckAddresses.IE);
        int ifFlag = memory.read(DuckAddresses.INTERRUPT_FLAG);
        boolean interruptPending = (ie & ifFlag & 0x1F) != 0;
        if (!cpu.isInterruptMasterEnable() && interruptPending) {
            // TODO: Halt bug
        } else {
            cpu.setHalted(true);
        }
        return 4;
    }

    
}
