package com.blackaby.Backend.Emulation.Memory;

/**
 * This class represents the memory of the GameBoy system.
 * It has methods for reading and writing to memory.
 * It also contains constants for the memory addresses.
 * It also contains a methods for reading from and writing to the stack with a
 * specified offset
 */
public class DuckMemory {
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
    public static final int IO_REGISTERS_END = 0xFF7F;
    public static final int HRAM_START = 0xFF80;
    public static final int HRAM_END = 0xFFFE;
    public static final int STACK_POINTER_START = HRAM_END;
    public static final int INTERRUPT_ENABLE = 0xFFFF;

    // Memory Array
    private byte memory[] = new byte[MEMORY_SIZE];

    /**
     * Creates a new DuckMemory object with all memory values set to 0
     */
    public DuckMemory() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = 0;
        }
    }

    /**
     * Reads a byte from the specified address
     * 
     * @param address The address to read from
     * @return The byte at the specified address
     */
    public byte read(int address) {
        address = validate(address);
        return memory[address];
    }

    /**
     * Reads a byte from the specified address
     * 
     * @param address The address to read from
     * @return The byte at the specified address
     */
    public byte read(short address) {
        return read((int) address & 0xFFFF);
    }

    /**
     * Writes a byte to the specified address
     * 
     * @param address The address to write to
     * @param value   The value to write
     */
    public void write(int address, byte value) {
        address = validate(address);
        memory[address] = value;
    }

    /**
     * Writes a byte to the specified address
     * 
     * @param address The address to write to
     * @param value   The value to write
     */
    public void write(short address, byte value) {
        write((int) address & 0xFFFF, value);
    }

    /**
     * Pushes a byte onto the stack and updates the stack pointer.
     * 
     * @param sp    The current stack pointer address
     * @param value The byte to push onto the stack
     * @return The updated stack pointer after the push operation
     */
    public int stackPush(int sp, byte value) {
        sp = (sp - 1) & 0xFFFF; // Decrement stack pointer (wrap around 16-bit)
        write(sp, value);
        return sp;
    }

    /**
     * Pops a byte from the stack and updates the stack pointer.
     * 
     * @param sp The current stack pointer address
     * @return The byte popped from the stack
     */
    public byte stackPop(int sp) {
        byte value = read(sp);
        sp = (sp + 1) & 0xFFFF; // Increment stack pointer (wrap around 16-bit)
        return value;
    }

    private int validate(int address) {
        if (address < 0)
            return STACK_POINTER_START + address;
        if (address >= MEMORY_SIZE)
            return address % MEMORY_SIZE;
        return address;
    }
}
