package com.blackaby.Backend.Emulation.Misc;

/**
 * This class contains specific values for the GameBoy system.
 * These values are used by other classes to maintain consistency.
 */
public class Specifics {
    /**
     * Display Width of Game Boy in pixels.
     */
    public static final int GB_DISPLAY_WIDTH = 160;
    /**
     * Display Height of Game Boy in pixels.
     */
    public static final int GB_DISPLAY_HEIGHT = 144;
    /**
     * Refresh rate of Game Boy in frames per second.
     */
    public static final int REFRESH_RATE = 60;
    /**
     * Cycles Per Second (Clock Speed) Of Game Boy
     */
    public static final double CPS = 4194304;
    /**
     * Nanoseconds Per Cycle of Game Boy's clock
     */
    public static final double NS_PER_CYCLE = 1_000_000_000.0 / CPS;
}
