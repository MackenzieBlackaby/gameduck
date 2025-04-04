package com.blackaby.Backend.Emulation.Peripherals;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

/**
 * Emulates the Game Boy's programmable timer peripheral.
 * <p>
 * The timer consists of:
 * <ul>
 * <li>A 16-bit internal counter (used to drive DIV and TIMA)</li>
 * <li>A programmable frequency and enable control via TAC</li>
 * <li>A modulo register (TMA) used to reload TIMA on overflow</li>
 * </ul>
 * This class handles overflow, reload delay, and interrupt triggering.
 */
public class DuckTimer {
    private int internalCounter = 0;
    private boolean previousTimerBit = false;

    // 0 means no pending reload; 1 means reload next tick
    private int overflowCounter = 0;

    private DuckMemory memory;
    private DuckCPU cpu;

    /**
     * Constructs a DuckTimer linked to the CPU and memory.
     *
     * @param cpu    the Game Boy CPU instance
     * @param memory the shared memory bus
     */
    public DuckTimer(DuckCPU cpu, DuckMemory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    /**
     * Advances the timer by one cycle.
     * <p>
     * Handles internal counter increment, DIV update, TIMA incrementing,
     * and pending overflow resolution.
     * </p>
     */
    public void tick() {
        // 1. If there's an overflow pending from the *previous* cycle, reload now
        if (overflowCounter == 1) {
            memory.write(DuckMemory.TIMA, memory.read(DuckMemory.TMA));
            cpu.requestInterrupt(DuckCPU.Interrupt.TIMER);
            overflowCounter = 0;
        }

        // 2. Increment the 16-bit internal counter
        internalCounter = (internalCounter + 1) & 0xFFFF;

        // 3. Write top 8 bits to DIV
        int divValue = (internalCounter >> 8) & 0xFF;
        memory.write(DuckMemory.DIV, divValue);

        // 4. Update TIMA (might detect a new overflow)
        updateTIMA();
    }

    private void updateTIMA() {
        int tac = memory.read(DuckMemory.TAC);
        boolean timerEnabled = (tac & 0x04) != 0;
        int inputClockSelect = tac & 0x03;

        // Determine which bit to monitor
        int monitoredBit;
        switch (inputClockSelect) {
            case 0:
                monitoredBit = 9;
                break; // 4096 Hz
            case 1:
                monitoredBit = 3;
                break; // 262144 Hz
            case 2:
                monitoredBit = 5;
                break; // 65536 Hz
            case 3:
                monitoredBit = 7;
                break; // 16384 Hz
            default:
                monitoredBit = 9;
                break;
        }

        boolean currentTimerBit = timerEnabled && ((internalCounter & (1 << monitoredBit)) != 0);

        // On falling edge (1->0), increment TIMA or handle overflow
        if (previousTimerBit && !currentTimerBit) {
            int tima = memory.read(DuckMemory.TIMA) & 0xFF;
            if (tima == 0xFF) {
                // Overflow => write 0x00 now, schedule reload for *next* cycle
                memory.write(DuckMemory.TIMA, 0x00);
                overflowCounter = 1;
            } else {
                // Normal increment
                memory.write(DuckMemory.TIMA, (tima + 1) & 0xFF);
            }
        }

        previousTimerBit = currentTimerBit;
    }

    /**
     * Resets the internal counter and clears the DIV register.
     * Called when writing to DIV.
     */
    public void resetDIV() {
        internalCounter = 0;
    }

    /**
     * Returns the current value of the internal 16-bit counter.
     *
     * @return the internal counter value
     */
    public int getInternalCounter() {
        return internalCounter;
    }
}
