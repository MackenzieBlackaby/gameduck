package com.blackaby.Backend.Emulation.Memory;

import com.blackaby.Backend.Emulation.Misc.ROM;
import com.blackaby.OldBackEnd.Emulation.Peripherals.DuckTimer;

/**
 * Represents the Game Boy's memory system.
 * <p>
 * Provides read/write access to all memory regions, including handling for:
 * - Echo RAM
 * - DMA transfers
 * - Stack operations
 * - Special register behaviour (e.g., DIV reset, DMA trigger)
 * </p>
 * Supports loading a ROM and provides access to memory-mapped IO.
 */

public class DuckMemory {

    /**
     * Represents the Memory Bank Controller (MBC) type in use by the cartridge.
     */
    public enum MBCType {
        ROM_ONLY, MBC1, MBC2, MBC3, MBC5, UNKNOWN
    }

    private int ram[];
    private int rom[];
    private int romBank = 0;
    private DuckTimer timerSet;
    private boolean dmaActive = false;
    private int dmaCounter = 0;
    private int dmaSource = 0;

    /**
     * Constructs a new memory instance and clears RAM.
     */
    public DuckMemory() {
        this.ram = new int[DuckAddresses.MEMORY_SIZE];
        this.rom = new int[0];
    }

    /**
     * Sets the attached timer component used for timing registers.
     *
     * @param timerSet The timer to bind.
     */
    public void setTimerSet(DuckTimer timerSet) {
        this.timerSet = timerSet;
    }

    /**
     * Loads a ROM into memory, copying ROM contents into address space.
     *
     * @param rom The ROM object containing cartridge data.
     */
    public void loadROM(ROM rom) {
        this.rom = rom.getData();
        ram = new int[DuckAddresses.MEMORY_SIZE];
        System.arraycopy(this.rom, 0, ram, 0, this.rom.length);
    }

    /**
     * Reads a byte from memory, with special handling for echo RAM and special
     * registers.
     *
     * @param address The 16-bit memory address to read from.
     * @return The 8-bit value at that address.
     */
    public int read(int address) {
        // Defensive masking
        address &= 0xFFFF;

        // Handle div timer
        if (address == DuckAddresses.DIV) {
            return timerSet.getInternalCounter() >> 8;
        }

        // Handle rom banks
        if (address >= DuckAddresses.ROM_BANK_N_START && address <= DuckAddresses.ROM_BANK_N_END) {
            int offset = address - DuckAddresses.ROM_BANK_N_START;
            int actualAddress = (romBank * 0x4000) + offset;
            return rom[actualAddress];
        }

        // Handle Echo Ram
        if (address >= DuckAddresses.ECHO_RAM_START && address <= DuckAddresses.ECHO_RAM_END) {
            int idx = (address - DuckAddresses.ECHO_RAM_START + DuckAddresses.WORK_RAM_START) & 0xFFFF;
            return 0xFF & ram[idx];
        }

        // Handle unusable memory
        if (address >= DuckAddresses.NOT_USABLE_START && address <= DuckAddresses.NOT_USABLE_END) {
            return 0xFF;
        }

        // Handle other memory
        return 0xFF & ram[address];
    }

    /**
     * Writes a byte to memory, with handling for echo RAM, DIV resets and DMA
     * triggers.
     *
     * @param address The memory address to write to.
     * @param value   The 8-bit value to write.
     */
    public void write(int address, int value) {
        // Defensive masking
        address &= 0xFFFF;
        value &= 0xFF;

        // Writes to rom area (0x0000 - 0x7FFF)
        if (address < 0x8000) {
            // Handle ROM Banking (0x2000 - 0x3FFF)
            if (address >= 0x2000 && address <= 0x3FFF) {
                int bank = value & 0x1F; // Mask to 5 bits
                if (bank == 0)
                    bank = 1;
                this.romBank = bank;
            }
            // Handle RAM Banking (0x4000 - 0x5FFF)
            else if (address >= 0x4000 && address <= 0x5FFF) {
                // RAM bank logic (if you implement External RAM later)
            }
            return;
        }

        // Block Unusable memory
        if (address >= DuckAddresses.NOT_USABLE_START && address <= DuckAddresses.NOT_USABLE_END) {
            return;
        }
        // Echo RAM
        if (address >= DuckAddresses.ECHO_RAM_START && address <= DuckAddresses.ECHO_RAM_END) {
            int idx = (address - DuckAddresses.ECHO_RAM_START + DuckAddresses.WORK_RAM_START);
            ram[idx] = value;
            return;
        }
        // Div writes
        if (address == DuckAddresses.DIV) {
            timerSet.resetDIV();
            return;
        }
        // Tima writes
        if (address == DuckAddresses.TIMA) {
            if (timerSet.timaOverflowPending) {
                timerSet.cancelPendingOverflow();
            }
        }

        // Writing to ram
        ram[address] = value;

        // DMA
        if (address == DuckAddresses.DMA) {
            dmaSource = (value & 0xFF) << 8;
            dmaCounter = 0;
            dmaActive = true;
        }
    }

    /**
     * Emulates a single DMA transfer cycle, copying one byte to OAM.
     */
    public void tickDMA() {
        // Block if not DMA active
        if (!dmaActive)
            return;

        // Calculating source and destination
        int dest = DuckAddresses.OAM_START + dmaCounter;
        int src = dmaSource + dmaCounter;

        // Defensive Masking
        dest &= 0xFFFF;
        src &= 0xFFFF;

        // Transfer byte of data
        int data = read(src);
        ram[dest] = data; // write into OAM area

        // Tick DMA Counter
        dmaCounter++;

        // If DMA is finished
        if (dmaCounter == 0xA0) {
            dmaActive = false;
            dmaCounter = 0;
        }
    }

    /**
     * Reads a sequence of bytes from memory.
     *
     * @param start The start address.
     * @param count The number of bytes to read.
     * @return An array of 8-bit values.
     */
    public int[] readBytes(int start, int count) {
        int[] bytes = new int[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = read(start + i);
        }
        return bytes;
    }

    public void setDividerFromTimer(int value) {
        // Defensive masking
        value &= 0xFF;
        ram[DuckAddresses.DIV] = value;
    }

    public void setTIMAFromTimer(int value) {
        // Defensive Masking
        value &= 0xFF;
        ram[DuckAddresses.TIMA] = value;
    }

}
