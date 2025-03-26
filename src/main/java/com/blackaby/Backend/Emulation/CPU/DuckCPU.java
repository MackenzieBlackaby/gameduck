package com.blackaby.Backend.Emulation.CPU;

import com.blackaby.Frontend.DebugLogger;
import com.blackaby.Backend.Emulation.DuckEmulation;
import com.blackaby.Backend.Emulation.Memory.DuckMemory;

/**
 * This class represents the CPU of the GameBoy.
 * It has methods for executing instructions and managing registers.
 * It also has methods for reading and writing to memory.
 * It is the main class for the CPU emulation.
 * 
 * The instructions are implemented as methods in this class.
 * The CPU has a reference to the memory and the display.
 */
public class DuckCPU {

    /**
     * This enum represents the registers of the CPU.
     * It has values for all 8-bit and 16-bit registers.
     * It also has values for special registers like the flags and the instruction
     * register.
     */
    public enum Register {

        // 8 bit registers
        B(0), // 000
        C(1), // 001
        D(2), // 010
        E(3), // 011
        H(4), // 100
        L(5), // 101
        HL_ADDR(6), // 110
        A(7), // 111

        // Special registers
        F(8), // Flags
        IR(9), // Instruction Register
        IE(10), // Interrupt Enable

        // 16-bit registers
        BC(11), // 00
        DE(12), // 01
        HL(13), // 10
        AF(14), // 11
        SP(15),
        PC(16);

        private final int id;

        Register(int id) {
            this.id = id;
        }

        public boolean is8Bit() {
            if (id < 0 || id > 10)
                return false;
            return true;
        }

        public static Register get8Bit(int id) {
            if (id < 0 || id > 10) { // Updated range check for 8-bit registers
                throw new IllegalArgumentException("Invalid 8-bit register ID: " + id);
            }
            return Register.values()[id];
        }

        public static Register get16Bit(int id) {
            if (id < 11 || id > 16) { // Updated range check for 16-bit registers
                throw new IllegalArgumentException("Invalid 16-bit register ID: " + id);
            }
            return Register.values()[id];
        }

        public static Register getRegFrom2Bit(int bitID, boolean isAFContext) {
            bitID &= 0b11; // Mask to 2 bits
            switch (bitID) {
                case 0b00:
                    return Register.BC;
                case 0b01:
                    return Register.DE;
                case 0b10:
                    return Register.HL;
                case 0b11:
                    return isAFContext ? Register.AF : Register.SP;
                default:
                    throw new IllegalArgumentException("Invalid 16-bit register ID: " + bitID);
            }
        }

        public static Register getRegFrom3Bit(int bitID) {
            bitID &= 0b111; // Mask to 3 bits
            switch (bitID) {
                case 0b000:
                    return Register.B;
                case 0b001:
                    return Register.C;
                case 0b010:
                    return Register.D;
                case 0b011:
                    return Register.E;
                case 0b100:
                    return Register.H;
                case 0b101:
                    return Register.L;
                case 0b110:
                    return Register.HL_ADDR;
                case 0b111:
                    return Register.A;
                default:
                    throw new IllegalArgumentException("Invalid 8-bit register ID: " + bitID);
            }
        }

        public byte getId() {
            return (byte) id;
        }
    }

    /**
     * This enum represents the flags of the CPU.
     * It has values for all 4 flags: Zero, Subtract, Half Carry, and Carry.
     * Each flag has a bit position in the flags register.
     * The flags register is an 8-bit register, so each flag has a bit from 4 to 7.
     * The flags register is used to store the status of the CPU.
     */
    public enum Flag {
        Z(7), // Z
        N(6), // N
        H(5), // H
        C(4); // C

        private final int bit;

        /**
         * This constructor creates a new flag with the given bit position
         * 
         * @param bit The bit position of the flag
         */
        Flag(int bit) {
            this.bit = bit;
        }

        /**
         * This method returns the bit position of the flag
         * 
         * @return The bit position of the flag
         */
        public int getBit() {
            return bit;
        }

        public static Flag getFlagFrom2Bit(byte bitID) {
            bitID &= 0b11; // Mask to 2 bits
            switch (bitID) {
                case 0b00:
                    return Z;
                case 0b01:
                    return N;
                case 0b10:
                    return H;
                case 0b11:
                    return C;
                default:
                    throw new IllegalArgumentException("Invalid flag ID: " + bitID);
            }
        }
    }

    /**
     * This enum represents the interrupts of the CPU.
     * It has values for all 5 interrupts: VBLANK, LCD_STAT, TIMER, SERIAL, and
     * JOYPAD.
     * Each interrupt has a mask value that is used to enable or disable the
     * interrupt.
     */
    public enum Interrupt {
        VBLANK(0x01, 0x40), // Interrupt 0
        LCD_STAT(0x02, 0x48), // Interrupt 1
        TIMER(0x04, 0x50), // Interrupt 2
        SERIAL(0x08, 0x58), // Interrupt 3
        JOYPAD(0x10, 0x60); // Interrupt 4

        private final int mask;
        private final short address;

        Interrupt(int mask, int address) {
            this.mask = mask;
            this.address = (short) address;
        }

        public int getMask() {
            return mask;
        }

        public short getAddress() {
            return address;
        }

        public static Interrupt getInterrupt(int index) {
            switch (index) {
                case 0:
                    return VBLANK;
                case 1:
                    return LCD_STAT;
                case 2:
                    return TIMER;
                case 3:
                    return SERIAL;
                case 4:
                    return JOYPAD;
                default:
                    throw new IllegalArgumentException("Invalid interrupt index: " + index);
            }
        }
    }

    // Registers
    private int programCounter;
    private int stackPointer;
    private int flags;
    private int accumulator;
    private int registerB;
    private int registerC;
    private int registerD;
    private int registerE;
    private int registerH;
    private int registerL;
    private int instructionRegister;
    private boolean interruptMasterEnable;
    private int interruptMasterEnableCounter = 0;

    private boolean haltBug = false;

    private boolean isHalted = false;
    private boolean isStopped = false;

    public final DuckMemory memory;
    public final DuckEmulation emulation;

    public DuckCPU(DuckMemory memory, DuckEmulation emulation) {
        this.memory = memory;
        this.emulation = emulation;
    }

    public void setHalted(boolean halted) {
        isHalted = halted;
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public boolean isStopped() {
        return isStopped;
    }

    public boolean isHalted() {
        return isHalted;
    }

    /**
     * This method executes the next instruction in the queue
     */
    public int execute(Instruction instruction) {
        handleInterrupts();
        if (haltBug)
            haltBug = false;
        if (instruction != null && !isHalted) {
            instruction.run();
        }
        if (interruptMasterEnableCounter >= 2) {
            interruptMasterEnable = true;
            interruptMasterEnableCounter = 0;
        } else if (interruptMasterEnableCounter == 1) {
            interruptMasterEnableCounter++;
        }
        // memory.printStack(stackPointer);
        return instruction.getCycleCount();
    }

    public String toString() {
        // Using String.format for consistent hex formatting:
        String af = String.format("0x%02X%02X", accumulator, flags);
        String bc = String.format("0x%04X", getBCValue());
        String de = String.format("0x%04X", getDEValue());
        String hl = String.format("0x%04X", getHLValue());
        String pc = String.format("0x%04X", programCounter);
        String sp = String.format("0x%04X", stackPointer);
        String ir = String.format("0x%02X", instructionRegister);
        String ie = String.format("0x%02X", memory.read(DuckMemory.IE));

        // Extract individual flags (assuming your Flag enum uses bits as defined)
        boolean flagZ = getFlagBoolean(Flag.Z);
        boolean flagN = getFlagBoolean(Flag.N);
        boolean flagH = getFlagBoolean(Flag.H);
        boolean flagC = getFlagBoolean(Flag.C);

        String imeStatus = interruptMasterEnable ? "Enabled" : "Disabled";

        return String.format(
                "CPU State:\n" +
                        "  PC: %s    SP: %s\n" +
                        "  AF: %s    (A: 0x%02X, F: 0x%02X)\n" +
                        "  BC: %s    (B: 0x%02X, C: 0x%02X)\n" +
                        "  DE: %s    (D: 0x%02X, E: 0x%02X)\n" +
                        "  HL: %s    (H: 0x%02X, L: 0x%02X)\n" +
                        "  IR: %s\n" +
                        "  Flags: Z=%b  N=%b  H=%b  C=%b\n" +
                        "  IME: %s    IE: %s",
                pc, sp,
                af, accumulator, flags,
                bc, (registerB & 0xFF), (registerC & 0xFF),
                de, (registerD & 0xFF), (registerE & 0xFF),
                hl, (registerH & 0xFF), (registerL & 0xFF),
                ir,
                flagZ, flagN, flagH, flagC,
                imeStatus, ie);
    }

    /**
     * This method sets the value of the given register
     * 
     * @param reg   The register to set
     * @param value The value to set the register to
     * @throws IllegalArgumentException If the register is unknown
     */
    public void regSet(Register reg, int value) {
        value &= 0xFF; // Mask to 8-bit value
        switch (reg) {
            case A:
                accumulator = value;
                break;
            case F:
                flags = value;
                break;
            case B:
                registerB = value;
                break;
            case C:
                registerC = value;
                break;
            case D:
                registerD = value;
                break;
            case E:
                registerE = value;
                break;
            case H:
                registerH = value;
                break;
            case L:
                registerL = value;
                break;
            case IR:
                instructionRegister = value;
                break;
            case HL_ADDR:
                memory.write(getHLValue(), value);
                break;
            default:
                throw new IllegalArgumentException("Unknown 8-bit register: " + reg);
        }
    }

    /**
     * This method gets the value of the given register
     * 
     * @param reg The register to get
     * @return The value of the register
     * @throws IllegalArgumentException If the register is unknown
     */
    public int regGet(Register reg) {
        switch (reg) {
            case A:
                return accumulator;
            case F:
                return flags;
            case B:
                return registerB;
            case C:
                return registerC;
            case D:
                return registerD;
            case E:
                return registerE;
            case H:
                return registerH;
            case L:
                return registerL;
            case IR:
                return instructionRegister;
            case HL_ADDR:
                return 0xFF & memory.read(getHLValue());
            default:
                throw new IllegalArgumentException("Unknown 8-bit register: " + reg);
        }
    }

    /**
     * This method gets a specified 16-bit register
     * 
     * @param reg the 16-bit register to get
     * @return the value of the register
     */
    public int regGet16(Register reg) {
        switch (reg) {
            case PC:
                return programCounter;
            case SP:
                return stackPointer;
            case BC:
                return ((registerB & 0xFF) << 8) | (registerC & 0xFF);
            case DE:
                return ((registerD & 0xFF) << 8) | (registerE & 0xFF);
            case HL:
                return ((registerH & 0xFF) << 8) | (registerL & 0xFF);
            case AF:
                return ((accumulator & 0xFF) << 8) | (flags & 0xFF);
            default:
                throw new IllegalArgumentException("Invalid 16-bit register: " + reg);
        }
    }

    /**
     * This method sets a specified 16-bit register
     * 
     * @param reg   the 16-bit register to set
     * @param value the value to set the register to
     */
    public void regSet16(Register reg, int value) {
        value &= 0xFFFF; // Mask to 16-bit value
        switch (reg) {
            case PC:
                programCounter = value;
                break;
            case SP:
                stackPointer = value;
                break;
            case BC:
                registerB = (value >> 8) & 0xFF;
                registerC = value & 0xFF;
                break;
            case DE:
                registerD = (value >> 8) & 0xFF;
                registerE = value & 0xFF;
                break;
            case HL:
                registerH = (value >> 8) & 0xFF;
                registerL = value & 0xFF;
                break;
            case AF:
                accumulator = (value >> 8) & 0xFF;
                flags = value & 0xFF;
                break;
            default:
                throw new IllegalArgumentException("Invalid 16-bit register: " + reg);
        }
    }

    /**
     * This method gets the value of the HL register as a short
     * Provides Quick access to HL register without a switch case
     * 
     * @return The value of the HL register as a short
     */
    public int getHLValue() {
        int h = 0xFF & registerH;
        int l = 0xFF & registerL;
        int hl = (h << 8) | l;
        return 0xFFFF & hl;
    }

    /**
     * This method gets the value of the BC register as a short
     * Provides Quick access to BC register without a switch case
     * 
     * @return The value of the BC register as a short
     */
    public int getBCValue() {
        int b = 0xFF & registerB;
        int c = 0xFF & registerC;
        int bc = 0xFFFF & ((b << 8) | c);
        return bc;
    }

    public void setBC(int value) {
        registerB = (value >> 8) & 0xFF;
        registerC = 0xFF & value;
    }

    /**
     * This method gets the value of the DE register as a short
     * Provides Quick access to DE register without a switch case
     * 
     * @return The value of the DE register as a short
     */
    public int getDEValue() {
        int d = registerD & 0xFF;
        int e = registerE & 0xFF;
        int de = 0xFFFF & ((d << 8) | e);
        return de;
    }

    /**
     * This method gets the value of the program counter register as a short
     * Provides Quick access to program counter register without a switch case
     * 
     * @return The value of the program counter register as a short
     */
    public int getSP() {
        return 0xFFFF & stackPointer;
    }

    /**
     * This method gets the value of the accumulator register as a byte
     * Provides Quick access to accumulator register without a switch case
     * 
     * @return The value of the accumulator register as a byte
     */
    public int getAccumulator() {
        return 0xFF & accumulator;
    }

    /**
     * This method sets the value of the accumulator register
     * 
     * @param value The value to set the accumulator register to
     */
    public void setAccumulator(int value) {
        accumulator = 0xFF & value;
    }

    /**
     * This method gets the value of the flags register as a byte
     * Provides Quick access to flags register without a switch case
     * 
     * @return The value of the flags register as a byte
     */
    public void setHL(int value) {
        registerH = (value >> 8) & 0xFF;
        registerL = 0xFF & value;
    }

    public void setAF(int value) {
        accumulator = (value >> 8) & 0xFF;
        flags = 0xFF & value;
    }

    public void setDE(int value) {
        registerD = (value >> 8) & 0xFF;
        registerE = 0xFF & value;
    }

    /**
     * This method gets the value of the program counter register as a short
     * Provides Quick access to program counter register without a switch case
     * 
     * @return The value of the program counter register as a short
     */
    public int getPC() {
        return 0xFFFF & programCounter;
    }

    /**
     * This method sets the value of the program counter register
     * 
     * @param value The value to set the program counter register to
     */
    public void setPC(int value) {
        programCounter = 0xFFFF & value;
    }

    /**
     * This method gets the value of the flags register as a byte
     * Provides Quick access to flags register without a switch case
     * 
     * @return The value of the flags register as a byte
     */
    public int getC() {
        return 0xFF & registerC;
    }

    public void setSP(int value) {
        stackPointer = 0xFFFF & value;
    }

    public int getF() {
        return flags;
    }

    /**
     * This method activates given flags in the flags register
     * 
     * @param flagsToSet The flags to activate
     */
    public void activateFlags(Flag... flagsToSet) {
        for (Flag flag : flagsToSet) {
            this.flags |= 1 << flag.getBit();
        }
    }

    public void setHaltBug(boolean value) {
        haltBug = value;
    }

    public boolean getHaltBug() {
        return haltBug;
    }

    /**
     * This method sets the value of a flag in the flags register
     * 
     * @param flag  The flag to set
     * @param value The value to set the flag to
     */
    public void setFlag(Flag flag, boolean value) {
        if (value) {
            this.flags |= 1 << flag.getBit();
        } else {
            this.flags &= ~(1 << flag.getBit());
        }
    }

    /**
     * This method gets the value of a flag in the flags register as a boolean
     * 
     * @param flag The flag to get
     * @return The value of the flag as a boolean
     */
    public boolean getFlagBoolean(Flag flag) {
        return (flags & (1 << flag.getBit())) != 0;
    }

    /**
     * This method deactivates given flags in the flags register
     * 
     * @param flagsToClear The flags to deactivate
     */
    public void deactivateFlags(Flag... flagsToClear) {
        for (Flag flag : flagsToClear) {
            this.flags &= ~(1 << flag.getBit());
        }
    }

    public void requestInterrupt(Interrupt interrupt) {
        int interruptFlag = memory.read(DuckMemory.INTERRUPT_FLAG);
        memory.write(DuckMemory.INTERRUPT_FLAG, interruptFlag | interrupt.getMask());
    }

    public void handleInterrupts() {
        if (!interruptMasterEnable)
            return;
        int interruptEnable = memory.read(DuckMemory.IE);
        int interruptFlags = memory.read(DuckMemory.INTERRUPT_FLAG);
        int interruptsTriggered = interruptEnable & interruptFlags;

        if (interruptsTriggered == 0)
            return;

        interruptMasterEnable = false;
        for (int i = 0; i < 5; i++) {
            if ((interruptsTriggered & (1 << i)) != 0) {
                handleInterrupt(i);
                break;
            }
        }
    }

    public void handleInterrupt(int interruptBit) {
        DebugLogger.log("Handling interrupt: " + Interrupt.getInterrupt(interruptBit));
        int address = Interrupt.getInterrupt(interruptBit).getAddress();
        int interruptFlag = memory.read(DuckMemory.INTERRUPT_FLAG);
        memory.write(DuckMemory.INTERRUPT_FLAG, interruptFlag & ~(1 << interruptBit));
        // Write high byte
        memory.write(stackPointer - 1, (programCounter >> 8) & 0xFF);
        // Write low byte
        memory.write(stackPointer - 2, programCounter & 0xFF);
        stackPointer -= 2;
        programCounter = address;
    }

    public void setInterruptEnable(boolean enabled) {
        if (enabled)
            interruptMasterEnableCounter = 1;
        else {
            interruptMasterEnable = false;
            interruptMasterEnableCounter = 0;
        }
    }

    public boolean isInterruptMasterEnable() {
        return interruptMasterEnable;
    }

    public void setInterruptMasterEnable(boolean enabled) {
        interruptMasterEnable = enabled;
    }
}
