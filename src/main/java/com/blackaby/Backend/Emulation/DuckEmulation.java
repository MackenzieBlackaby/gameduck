package com.blackaby.Backend.Emulation;

import com.blackaby.Backend.Emulation.CPU.*;
import com.blackaby.Backend.Emulation.Misc.Specifics;
import com.blackaby.Frontend.DuckDisplay;

/**
 * This class represents the emulation of the GameBoy
 * It has methods for starting the emulation and getting the CPU and display
 */
public class DuckEmulation implements Runnable {

    // Emulated Hardware Parts
    private DuckCPU cpu;
    private DuckDisplay display;

    // Threading Variables
    private Thread emulationThread;
    private volatile boolean running = false;
    private volatile boolean paused = false;

    /**
     * This constructor creates a new DuckEmulation
     * 
     * @param display The display to be used in the emulation
     */
    public DuckEmulation(DuckDisplay display) {
        cpu = new DuckCPU(this);
        this.display = display;
        running = false;
        paused = false;
        emulationThread = new Thread(this);
        emulationThread.start();
    }

    /**
     * This method returns the CPU of the emulation
     * 
     * @return The CPU of the emulation
     */
    public DuckCPU getCPU() {
        return cpu;
    }

    /**
     * This method returns the display of the emulation
     * 
     * @return The display of the emulation
     */
    public DuckDisplay getDisplay() {
        return display;
    }

    @Override
    public void run() {
        running = true;
        long lastFrameTime = System.currentTimeMillis();
        while (running) {
            while (paused)
                ;
            if (System.currentTimeMillis() - lastFrameTime >= Specifics.CYCLE_DELAY) {
                cpu.queueInstruction(ReadNextInstruction());
                cpu.executeNextInstruction();
                lastFrameTime = System.currentTimeMillis();
            }
        }
    }

    private int ReadNextInstruction() {
        return 0xFF;
    }
}
