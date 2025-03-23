package com.blackaby.Backend.Emulation.Peripherals;

import com.blackaby.Backend.Emulation.CPU.DuckCPU;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

public class DuckTimer {
    private int internalCounter = 0;
    private boolean previousTimerBit = false;

    // 0 means no pending reload; 1 means reload next tick
    private int overflowCounter = 0;

    private DuckMemory memory;
    private DuckCPU cpu;

    public DuckTimer(DuckCPU cpu, DuckMemory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

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

    public void resetDIV() {
        internalCounter = 0;
    }

    public int getInternalCounter() {
        return internalCounter;
    }
}
