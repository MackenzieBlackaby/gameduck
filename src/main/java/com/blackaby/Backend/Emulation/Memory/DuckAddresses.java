package com.blackaby.Backend.Emulation.Memory;

/**
 * This file contains constant addresses in memory and descriptors of each
 * This is to maintain an "address book" file and reduce complexity of other
 * java sections
 */
public class DuckAddresses {
    /** Size of the full address space (64 KiB). */
    public static final int MEMORY_SIZE = 0x10000;
    /** Start of fixed ROM bank (bank 0) that always maps to 0x0000-0x3FFF. */
    public static final int ROM_BANK_0_START = 0x0000;
    /** End of fixed ROM bank (bank 0). */
    public static final int ROM_BANK_0_END = 0x3FFF;
    /** Start of switchable ROM bank area. */
    public static final int ROM_BANK_N_START = 0x4000;
    /** End of switchable ROM bank area. */
    public static final int ROM_BANK_N_END = 0x7FFF;
    /** Start of video RAM (tile data and tile maps). */
    public static final int VRAM_START = 0x8000;
    /** End of video RAM. */
    public static final int VRAM_END = 0x9FFF;
    /** Start of cartridge external RAM (if present on the cart). */
    public static final int EXTERNAL_RAM_START = 0xA000;
    /** End of cartridge external RAM. */
    public static final int EXTERNAL_RAM_END = 0xBFFF;
    /** Start of working RAM (WRAM0). */
    public static final int WORK_RAM_START = 0xC000;
    /** End of working RAM (WRAM0/1). */
    public static final int WORK_RAM_END = 0xDFFF;
    /** Start of echo RAM (mirror of 0xC000-0xDDFF). */
    public static final int ECHO_RAM_START = 0xE000;
    /** End of echo RAM mirror region. */
    public static final int ECHO_RAM_END = 0xFDFF;
    /** Start of sprite attribute table (OAM). */
    public static final int OAM_START = 0xFE00;
    /** End of sprite attribute table (OAM). */
    public static final int OAM_END = 0xFE9F;
    /** Unusable hardware region between OAM and I/O. */
    public static final int NOT_USABLE_START = 0xFEA0;
    /** End of unusable hardware region. */
    public static final int NOT_USABLE_END = 0xFEFF;
    /** Start of hardware I/O registers. */
    public static final int IO_REGISTERS_START = 0xFF00;
    /** Interrupt flag register address. */
    public static final int INTERRUPT_FLAG = 0xFF0F;
    /** End of hardware I/O registers (before HRAM). */
    public static final int IO_REGISTERS_END = 0xFF7F;
    /** Start of high RAM (fast 127-byte internal RAM). */
    public static final int HRAM_START = 0xFF80;
    /** End of high RAM. */
    public static final int HRAM_END = 0xFFFE;
    /** Stack pointer typically begins at the end of HRAM. */
    public static final int STACK_POINTER_START = HRAM_END;
    /** Serial transfer data register. */
    public static final int SERIAL_DATA = 0xFF01;
    /** Serial transfer control register. */
    public static final int SERIAL_CONTROL = 0xFF02;
    /** LCD control register. */
    public static final int LCDC = 0xFF40;
    /** LCD Y-coordinate (current scanline). */
    public static final int LY = 0xFF44;
    /** Scroll Y register. */
    public static final int SCY = 0xFF42;
    /** Scroll X register. */
    public static final int SCX = 0xFF43;
    /** Background palette (DMG). */
    public static final int BGP = 0xFF47;
    /** Divider register (increments at 16384 Hz). */
    public static final int DIV = 0xFF04;
    /** Timer counter register. */
    public static final int TIMA = 0xFF05;
    /** Timer modulo register (reload value). */
    public static final int TMA = 0xFF06;
    /** Timer control register (frequency and enable). */
    public static final int TAC = 0xFF07;
    /** Joypad input register. */
    public static final int JOYPAD = 0xFF00;
    /** LCD status register. */
    public static final int STAT = 0xFF41;
    /** DMA transfer start address register (OAM DMA). */
    public static final int DMA = 0xFF46;
    /** Interrupt enable register. */
    public static final int IE = 0xFFFF;
}
