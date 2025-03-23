package com.blackaby.Backend.Emulation.CPU;

import com.blackaby.Backend.Emulation.CPU.DuckCPU.Interrupt;
import com.blackaby.Backend.Emulation.Graphics.GBColor;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;
import com.blackaby.Backend.Emulation.Misc.Specifics;
import com.blackaby.Frontend.DuckDisplay;
import com.blackaby.Misc.Settings;
import com.blackaby.Frontend.DebugLogger;

/**
 * DuckPPU class with corrected VBLANK timing.
 */
public class DuckPPU {

    // Mode durations for visible lines:
    public static final int OAM_DURATION = 80; // OAM scan
    public static final int VRAM_DURATION = 172; // VRAM scan
    public static final int HBLANK_DURATION = 204; // H-Blank
    // Each visible line is 80 + 172 + 204 = 456 cycles total.

    // The total number of cycles each scanline (including V-Blank) should take:
    private static final int SCANLINE_CYCLES = 456;

    // The total lines on the Game Boy are 154 (0–153).
    // Lines 0–143 = visible, lines 144–153 = V-Blank.

    private enum PPUMode {
        HBLANK,
        VBLANK,
        OAM,
        VRAM
    }

    private DuckCPU cpu;
    private DuckMemory memory;
    private DuckDisplay display;
    private PPUMode mode;
    private int scanline; // current LY
    private int cycle; // how many cycles have elapsed in the current mode

    /**
     * Constructor for the DuckPPU class.
     *
     * @param cpu     the DuckCPU object
     * @param memory  the DuckMemory object
     * @param display the DuckDisplay object
     */
    public DuckPPU(DuckCPU cpu, DuckMemory memory, DuckDisplay display) {
        this.cpu = cpu;
        this.memory = memory;
        this.display = display;
        this.scanline = 0;
        this.cycle = 0;
        this.mode = PPUMode.OAM;
        // You may want to initialize mode to OAM directly, or call ActivatePPU().
    }

    /**
     * Sets the PPU mode and updates STAT register accordingly.
     */
    private void setMode(PPUMode mode) {
        this.mode = mode;
        int stat = memory.read(0xFF41) & 0xFF;

        // Clear the last two bits, then set them according to the new mode ordinal.
        stat = (stat & 0xFC) | mode.ordinal();
        memory.write(0xFF41, (byte) stat);

        // If the corresponding STAT interrupt bit is enabled, request a STAT interrupt.
        // (bit 3 + mode.ordinal())
        boolean statInterrupt = (stat & (1 << (3 + mode.ordinal()))) != 0;
        if (statInterrupt) {
            DebugLogger.logn("[PPU DEBUG] STAT interrupt triggered for mode " + mode);
            cpu.requestInterrupt(Interrupt.LCD_STAT);
        }
    }

    /**
     * Steps the PPU by one cycle, advancing mode/scanline as needed.
     */
    public void step() {
        cycle++;

        switch (mode) {
            case OAM:
                if (cycle >= OAM_DURATION) {
                    cycle = 0;
                    setMode(PPUMode.VRAM);
                }
                break;
            case VRAM:
                if (cycle >= VRAM_DURATION) {
                    cycle = 0;
                    setMode(PPUMode.HBLANK);
                    // Render the current scanline after VRAM access completes.
                    renderScanline(scanline);
                }
                break;

            case HBLANK:
                if (cycle >= HBLANK_DURATION) {
                    cycle = 0;
                    scanline++;
                    // Once we hit line 144, enter VBLANK and fire interrupt.
                    if (scanline == 144) {
                        setMode(PPUMode.VBLANK);
                        cpu.requestInterrupt(Interrupt.VBLANK);
                    } else {
                        setMode(PPUMode.OAM);
                    }
                }
                break;

            case VBLANK:
                // In VBLANK, each of lines 144–153 should still last 456 cycles.
                if (cycle >= SCANLINE_CYCLES) {
                    cycle = 0;
                    scanline++;
                    // Once we reach line 154, we wrap back to line 0 (start new frame).
                    if (scanline >= 154) {
                        scanline = 0;
                        setMode(PPUMode.OAM);
                    }
                    // Repaint the entire frame each time we advance a line in V-Blank.
                    display.repaint();
                }
                break;
        }

        // Update LY register (current scanline) and compare with LYC for interrupts.
        memory.write(DuckMemory.LY, (byte) scanline);
        updateLYCCompare();
    }

    /**
     * Renders the background layer of the specified scanline.
     * (You could also add sprite rendering, window, etc.)
     */
    public void renderScanline(int scanline) {
        // LCDC register controls whether BG is on/off, which tile map, etc.
        int lcdc = memory.read(0xFF40) & 0xFF;

        // If BG display is disabled, fill the line with white and return.
        if ((lcdc & 0x01) == 0) {
            GBColor defaultColor = new GBColor(255, 255, 255); // White
            for (int x = 0; x < Specifics.GB_DISPLAY_WIDTH; x++) {
                display.setPixel(x, scanline, defaultColor.toColor(), false);
            }
            return;
        }

        // Get scroll offsets.
        int scrollY = memory.read(0xFF42) & 0xFF;
        int scrollX = memory.read(0xFF43) & 0xFF;

        // "World" position in the BG map:
        int worldY = (scanline + scrollY) & 0xFF;

        // Determine base addresses for tile map and tile data.
        int tileMapBase = ((lcdc & 0x08) != 0) ? 0x9C00 : 0x9800;
        boolean use8000 = ((lcdc & 0x10) != 0);
        int tileDataBase = use8000 ? 0x8000 : 0x9000;

        // BG palette.
        int bgPalette = memory.read(0xFF47) & 0xFF;

        // For each pixel in the scanline:
        for (int screenX = 0; screenX < Specifics.GB_DISPLAY_WIDTH; screenX++) {
            int worldX = (screenX + scrollX) & 0xFF;
            int tileColumn = worldX / 8;
            int tileRow = worldY / 8;

            // Find the tile ID in the BG map.
            int tileIndexAddr = tileMapBase + (tileRow * 32) + tileColumn;
            int tileID = memory.read(tileIndexAddr) & 0xFF;

            // In 0x8800 mode, the tile ID is signed (-128..127). Adjust base if so.
            if (!use8000) {
                tileID = (byte) tileID; // interpret as signed
            }

            // Each tile is 16 bytes, 2 bytes per row.
            int tileAddr = tileDataBase + (tileID * 16);
            int tileLine = worldY % 8;

            int byte1 = memory.read(tileAddr + (tileLine * 2)) & 0xFF;
            int byte2 = memory.read(tileAddr + (tileLine * 2 + 1)) & 0xFF;
            int bitIndex = 7 - (worldX % 8);

            // Combine bits from the two tile bytes to form a color index [0..3].
            int colorValue = (((byte2 >> bitIndex) & 1) << 1) |
                    ((byte1 >> bitIndex) & 1);

            // Convert that color index using the BG palette.
            int paletteShift = colorValue * 2;
            int paletteBits = (bgPalette >> paletteShift) & 0x03;

            GBColor pixelColor;
            switch (paletteBits) {
                case 0:
                    pixelColor = Settings.GB_COLOR_0_OBJ;
                    break;
                case 1:
                    pixelColor = Settings.GB_COLOR_1_OBJ;
                    break;
                case 2:
                    pixelColor = Settings.GB_COLOR_2_OBJ;
                    break;
                case 3:
                default:
                    pixelColor = Settings.GB_COLOR_3_OBJ;
                    break;
            }

            // Write the pixel to the display buffer.
            display.setPixel(screenX, scanline, pixelColor.toColor(), false);
        }
    }

    /**
     * Compares LY with LYC and sets the STAT register’s coincidence flag and
     * interrupt.
     */
    private void updateLYCCompare() {
        int ly = memory.read(DuckMemory.LY) & 0xFF;
        int lyc = memory.read(0xFF45) & 0xFF;
        int stat = memory.read(0xFF41) & 0xFF;

        if (ly == lyc) {
            // Set the coincidence flag (bit 2).
            stat |= 0x04;
            // If the LY=LYC interrupt (bit 6) is enabled, request a STAT interrupt.
            if ((stat & 0x40) != 0) {
                cpu.requestInterrupt(DuckCPU.Interrupt.LCD_STAT);
            }
        } else {
            // Clear the coincidence flag.
            stat &= ~0x04;
        }
        memory.write(0xFF41, (byte) stat);
    }
}
