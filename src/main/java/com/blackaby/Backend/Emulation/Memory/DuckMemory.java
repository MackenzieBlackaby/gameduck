package com.blackaby.Backend.Emulation.Memory;

import com.blackaby.Backend.Emulation.Misc.ROM;
import com.blackaby.Backend.Emulation.Peripherals.DuckTimer;
import com.blackaby.Frontend.DebugLogger;

public class DuckMemory {

    public enum MBCType {
        ROM_ONLY, MBC1, MBC2, MBC3, MBC5, UNKNOWN
    }

    public static int[] GB_BOOT = new int[] {
            0x31, 0xFE, 0xFF, 0xAF, 0x21, 0xFF, 0x9F, 0x32, 0xCB, 0x7C, 0x20, 0xFB, 0x21, 0x26, 0xFF, 0x0E,
            0x11, 0x3E, 0x80, 0x32, 0xE2, 0x0C, 0x3E, 0xF3, 0xE2, 0x32, 0x3E, 0x77, 0x77, 0x3E, 0xFC, 0xE0,
            0x47, 0x11, 0x04, 0x01, 0x21, 0x10, 0x80, 0x1A, 0xCD, 0x95, 0x00, 0xCD, 0x96, 0x00, 0x13, 0x7B,
            0xFE, 0x34, 0x20, 0xF3, 0x11, 0xD8, 0x00, 0x06, 0x08, 0x1A, 0x13, 0x22, 0x23, 0x05, 0x20, 0xF9,
            0x3E, 0x19, 0xEA, 0x10, 0x99, 0x21, 0x2F, 0x99, 0x0E, 0x0C, 0x3D, 0x28, 0x08, 0x32, 0x0D, 0x20,
            0xF9, 0x2E, 0x0F, 0x18, 0xF3, 0x67, 0x3E, 0x64, 0x57, 0xE0, 0x42, 0x3E, 0x91, 0xE0, 0x40, 0x04,
            0x1E, 0x02, 0x0E, 0x0C, 0xF0, 0x44, 0xFE, 0x90, 0x20, 0xFA, 0x0D, 0x20, 0xF7, 0x1D, 0x20, 0xF2,
            0x0E, 0x13, 0x24, 0x7C, 0x1E, 0x83, 0xFE, 0x62, 0x28, 0x06, 0x1E, 0xC1, 0xFE, 0x64, 0x20, 0x06,
            0x7B, 0xE2, 0x0C, 0x3E, 0x87, 0xE2, 0xF0, 0x42, 0x90, 0xE0, 0x42, 0x15, 0x20, 0xD2, 0x05, 0x20,
            0x4F, 0x16, 0x20, 0x18, 0xCB, 0x4F, 0x06, 0x04, 0xC5, 0xCB, 0x11, 0x17, 0xC1, 0xCB, 0x11, 0x17,
            0x05, 0x20, 0xF5, 0x22, 0x23, 0x22, 0x23, 0xC9, 0xCE, 0xED, 0x66, 0x66, 0xCC, 0x0D, 0x00, 0x0B,
            0x03, 0x73, 0x00, 0x83, 0x00, 0x0C, 0x00, 0x0D, 0x00, 0x08, 0x11, 0x1F, 0x88, 0x89, 0x00, 0x0E,
            0xDC, 0xCC, 0x6E, 0xE6, 0xDD, 0xDD, 0xD9, 0x99, 0xBB, 0xBB, 0x67, 0x63, 0x6E, 0x0E, 0xEC, 0xCC,
            0xDD, 0xDC, 0x99, 0x9F, 0xBB, 0xB9, 0x33, 0x3E, 0x3C, 0x42, 0xB9, 0xA5, 0xB9, 0xA5, 0x42, 0x3C,
            0x21, 0x04, 0x01, 0x11, 0xA8, 0x00, 0x1A, 0x13, 0xBE, 0x00, 0x00, 0x23, 0x7D, 0xFE, 0x34, 0x20,
            0xF5, 0x06, 0x19, 0x78, 0x86, 0x23, 0x05, 0x20, 0xFB, 0x86, 0x00, 0x00, 0x3E, 0x01, 0xE0, 0x50
    };

    // Memory Constants
    public static final int MEMORY_SIZE = 0x10000;
    public static final int ROM_BANK_0_START = 0x0000;
    public static final int ROM_BANK_0_END = 0x3FFF;
    public static final int ROM_BANK_N_START = 0x4000;
    public static final int ROM_BANK_N_END = 0x7FFF;
    public static final int VRAM_START = 0x8000;
    public static final int VRAM_END = 0x9FFF;
    public static final int EXTERNAL_RAM_START = 0xA000;
    public static final int EXTERNAL_RAM_END = 0xBFFF;
    public static final int WORK_RAM_START = 0xC000;
    public static final int WORK_RAM_END = 0xDFFF;
    public static final int ECHO_RAM_START = 0xE000;
    public static final int ECHO_RAM_END = 0xFDFF;
    public static final int OAM_START = 0xFE00;
    public static final int OAM_END = 0xFE9F;
    public static final int NOT_USABLE_START = 0xFEA0;
    public static final int NOT_USABLE_END = 0xFEFF;
    public static final int IO_REGISTERS_START = 0xFF00;
    public static final int INTERRUPT_FLAG = 0xFF0F;
    public static final int IO_REGISTERS_END = 0xFF7F;
    public static final int HRAM_START = 0xFF80;
    public static final int HRAM_END = 0xFFFE;
    public static final int STACK_POINTER_START = HRAM_END;
    public static final int SERIAL_DATA = 0xFF01;
    public static final int SERIAL_CONTROL = 0xFF02;
    public static final int LCDC = 0xFF40;
    public static final int LY = 0xFF44;
    public static final int SCY = 0xFF42;
    public static final int SCX = 0xFF43;
    public static final int BGP = 0xFF47;
    public static final int DIV = 0xFF04;
    public static final int TIMA = 0xFF05;
    public static final int TMA = 0xFF06;
    public static final int TAC = 0xFF07;
    public static final int JOYPAD = 0xFF00;
    public static final int STAT = 0xFF41;
    public static final int DMA = 0xFF46;
    public static final int IE = 0xFFFF;

    private int ram[];
    private int rom[];
    private int romBank = 1;
    private int ramBank = 0;
    private boolean ramEnabled = false;
    private boolean bankingMode = false;
    private MBCType mbcType;
    private int totalRomBanks;
    private int totalRamBanks;
    private DuckTimer timerSet;
    private boolean dmaActive = false;
    private int dmaCounter = 0;
    private int dmaSource = 0;

    public DuckMemory() {
        this.ram = new int[MEMORY_SIZE];
    }

    public void setTimerSet(DuckTimer timerSet) {
        this.timerSet = timerSet;
    }

    public void loadBootROM() {
        System.arraycopy(GB_BOOT, 0, ram, 0, GB_BOOT.length);
    }

    public void unmapRom() {
        for (int i = ROM_BANK_0_START; i <= ROM_BANK_0_END; i++) {
            ram[i] = 0;
        }
    }

    public void printStack(int sp) {
        for (int i = WORK_RAM_END; i >= sp; i--) {
            DebugLogger.logn("0x" + Integer.toHexString(ram[i]));
        }
    }

    public void printMemory() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            DebugLogger.log(Integer.toHexString(ram[i]) + " ");
        }
    }

    public void loadROM(ROM rom) {
        this.rom = rom.getData();

        // // Read Cartridge Type from ROM Header
        // int cartridgeType = this.rom[0x0147] & 0xFF;

        // switch (cartridgeType) {
        // case 0x00:
        // mbcType = MBCType.ROM_ONLY;
        // break;
        // case 0x01:
        // case 0x02:
        // case 0x03:
        // mbcType = MBCType.MBC1;
        // break;
        // case 0x05:
        // case 0x06:
        // mbcType = MBCType.MBC2;
        // break;
        // case 0x0F:
        // case 0x10:
        // case 0x11:
        // case 0x12:
        // case 0x13:
        // mbcType = MBCType.MBC3;
        // break;
        // case 0x19:
        // case 0x1A:
        // case 0x1B:
        // case 0x1C:
        // case 0x1D:
        // case 0x1E:
        // mbcType = MBCType.MBC5;
        // break;
        // default:
        // mbcType = MBCType.UNKNOWN;
        // break;
        // }

        // // Read ROM Size
        // int romSizeCode = rom.getData()[0x0148] & 0xFF;
        // totalRomBanks = 2 << romSizeCode; // ROM banks = 2^(n+1)

        // // Read RAM Size
        // int ramSizeCode = rom.getData()[0x0149] & 0xFF;
        // switch (ramSizeCode) {
        // case 0x00:
        // totalRamBanks = 0;
        // break;
        // case 0x01:
        // totalRamBanks = 1;
        // break; // 2 KB (MBC2 internal RAM)
        // case 0x02:
        // totalRamBanks = 1;
        // break; // 8 KB
        // case 0x03:
        // totalRamBanks = 4;
        // break; // 32 KB (4 banks)
        // case 0x04:
        // totalRamBanks = 16;
        // break; // 128 KB (16 banks)
        // case 0x05:
        // totalRamBanks = 8;
        // break; // 64 KB (8 banks)
        // default:
        // totalRamBanks = 0;
        // break;
        // }
        ram = new int[MEMORY_SIZE];
        System.arraycopy(this.rom, 0, ram, 0, this.rom.length);

        // DebugLogger.logn("MBC Type: " + mbcType);
        // DebugLogger.logn("Total ROM Banks: " + totalRomBanks);
        // DebugLogger.logn("Total RAM Banks: " + totalRamBanks);
    }

    public int read(int address) {
        if (address == DuckMemory.DIV) {
            return timerSet.getInternalCounter() >> 8;
        }
        if (address >= ECHO_RAM_START && address <= ECHO_RAM_END) {
            return ram[address - ECHO_RAM_START + WORK_RAM_START];
        }
        return 0xFF & ram[address];
    }

    public void write(int address, int value) {
        if (address >= NOT_USABLE_START && address <= NOT_USABLE_END) {
            return;
        }
        if (address >= ECHO_RAM_START && address <= ECHO_RAM_END) {
            ram[address - ECHO_RAM_START + WORK_RAM_START] = value;
            return;
        }
        if (address == DIV) {
            timerSet.resetDIV();
            ram[DIV] = 0;
            return;
        }
        ram[address] = value & 0xFF;
        if (address == DMA) {
            dmaSource = value << 8;
            dmaCounter = 0;
            dmaActive = true;
        }
    }

    public void tickDMA() {
        if (!dmaActive)
            return;
        ram[OAM_START + dmaCounter] = read(dmaSource + dmaCounter);
        dmaCounter++;
        if (dmaCounter == 0xA0) {
            dmaActive = false;
            dmaCounter = 0;
        }
    }

    public void stackPush(int address, int value) {
        write(address, value);
    }

    public int stackPop(int address) {
        return read(address);
    }

    public int[] readBytes(int start, int count) {
        int[] bytes = new int[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = 0xFF & read(start + i);
        }
        return bytes;
    }

    public void printVRAM() {
        for (int i = VRAM_START; i <= VRAM_END; i++) {
            // DebugLogger.log(Integer.toHexString(read(i)) + " ");
            if ((i + 1) % 16 == 0) {
                // DebugLogger.logn();
            }
        }
    }

    public int getIE() {
        return read(IE);
    }

    public int getIF() {
        return read(INTERRUPT_FLAG);
    }
}
